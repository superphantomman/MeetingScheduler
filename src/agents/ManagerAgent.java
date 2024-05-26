package jadelab1.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jadelab1.items.ManagerScheduler;
import jadelab1.items.OfferConverter;

import javax.swing.*;

/*TODO
 *  1. Agent wysyła swoje godziny do managera
 *  2. Manager odsyla potwierdzenie
 *  3. Agent jeśli nie otrzyma potwierdzenia, ponawia wiadomość, inaczej wraca do punktu 1
 *
 *  Kazda wiadomość powinna mieć swój id
 *  manager powinien znać id agentów
 *  zaimplementować klasy z zdjęcia
 *  */

/*TODO
 * Agent powinien kończyć konwersacje
 * Podejmować decyzje o najlepszej godzinie, wtedy gdy już zostaje ona wybrana
 * Powinno zostać wykorzystane do tego ManagerScheduler,  do wybrana najkorzystniejszej godziny
 *
 * */

public class ManagerAgent extends Agent {

    protected void setup() {

        var learnMeetingParticipants = new LearnMeetingParticipants(this);
        var managingCyclicBehaviour = new ManagingCyclicBehaviour(this);

        addBehaviour(learnMeetingParticipants);
        WakerBehaviour wakerBehaviour = new WakerBehaviour(this, 10000) {
            @Override
            protected void onWake() {
                final ManagerScheduler scheduler = new
                        ManagerScheduler(learnMeetingParticipants.getNumberOfMeetingAgents());
                managingCyclicBehaviour.setScheduler(scheduler);
                learnMeetingParticipants.setFinished(true);
                addBehaviour(managingCyclicBehaviour);
            }
        };
        addBehaviour(wakerBehaviour);
        //doDelete();
    }

    protected void takeDown() {
        displayResponse("See you");
    }

    public void displayResponse(String message) {
        JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.PLAIN_MESSAGE);
    }


}

class ManagingCyclicBehaviour extends CyclicBehaviour {
    private final ManagerAgent agent;
    private ManagerScheduler scheduler;


    public ManagingCyclicBehaviour(ManagerAgent agent) {
        super(agent);
        this.agent = agent;
    }

    public void setScheduler(ManagerScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void action() {
        ACLMessage message = agent.receive();
        if (message == null) {
            block();
            return;
        }

        String conversationId = message.getConversationId();
        String content = message.getContent();
        int performative = message.getPerformative();

        if (performative == ACLMessage.PROPOSE) {
            final boolean isBestHourChosen = scheduler.add(conversationId, OfferConverter.convert(content));
            displaysCurrentOffers(conversationId, content);
            if (isBestHourChosen) {
                informMeetingAgentsAboutEndOfMeetingPlanning();
                agent.displayResponse("Best hour for all meeting agents is " + scheduler.getBestMeetingTime());
            }
            else
                agent.send(createRespondMessage(message));
        }


    }

    public void displaysCurrentOffers(String id, String content) {
        System.out.println(("id: " + id + " offers " + OfferConverter.convert(content)));
    }
    private void informMeetingAgentsAboutEndOfMeetingPlanning() {
        for (int i = 0; i < scheduler.getNumberOfAgents(); i++) {
            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
            message.setContent("End");
            message.addReceiver(new AID("MeetingAgent" + i, AID.ISLOCALNAME));
            agent.send(message);
        }
    }

    private ACLMessage createRespondMessage(ACLMessage m) {
        ACLMessage message;
        message = m.createReply();
        message.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        return message;
    }

}


class LearnMeetingParticipants extends SimpleBehaviour {
    private int numberOfMeetingAgents = 0;
    private final ManagerAgent agent;
    private boolean isFinished = false;

    public LearnMeetingParticipants(ManagerAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage message = agent.receive();
        if (message == null) {
            block();
            return;
        }
        if (message.getPerformative() == ACLMessage.INFORM && message.getContent().equals("introduce")) {
            incrementNumberOfMeetingAgents();
            System.out.println("Manager received message from MeetingAgent" + getNumberOfMeetingAgents());
        }
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
        System.out.println("Manager received all messages");
    }

    @Override
    public boolean done() {
        return isFinished;
    }

    public int getNumberOfMeetingAgents() {
        return numberOfMeetingAgents;
    }

    public void incrementNumberOfMeetingAgents() {
        numberOfMeetingAgents++;
    }
}

