package jadelab1.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.tools.sniffer.Message;
import jadelab1.items.ManagerScheduler;

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
    private int sendTo = 0;
    private ManagerScheduler scheduler;

    protected void setup() {
        displayResponse("Hello, I am " + getAID().getLocalName());


        var learnMeetingParticipants = new LearnMeetingParticipants(this);
        var managingCyclicBehaviour = new ManagingCyclicBehaviour(this);

        addBehaviour(learnMeetingParticipants);
        WakerBehaviour wakerBehaviour = new WakerBehaviour(this, 5000) {
            @Override
            protected void onWake() {
                scheduler = new ManagerScheduler(learnMeetingParticipants.getNumberOfMeetingAgents());
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


    public int getSendTo() {
        sendTo = sendTo % 3 + 1;
        return sendTo;
    }
}

class ManagingCyclicBehaviour extends CyclicBehaviour {
    private final ManagerAgent agent;


    public ManagingCyclicBehaviour(ManagerAgent agent) {
        super(agent);
        this.agent = agent;
    }

    public void action() {
//        System.out.println("MA");
        ACLMessage message = agent.receive();
        if (message == null) {
            block();
            return;
        }

        System.out.println("ManagerAgent received");
        String content = message.getContent();
        int performative = message.getPerformative();
        if (performative == ACLMessage.PROPOSE) {
            System.out.println("ManagerAgent sendback proposal");
            System.out.println(content);
            agent.send(createRespondMessage(message));
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
    private boolean isFinished =false;
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

