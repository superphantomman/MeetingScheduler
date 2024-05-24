package jadelab1;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

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
public class ManagerAgent extends Agent {
    protected void setup() {
        displayResponse("Hello, I am " + getAID().getLocalName());
        addBehaviour(new ManagingCyclicBehaviour(this));
        //doDelete();
    }

    protected void takeDown() {
        displayResponse("See you");
    }

    public void displayResponse(String message) {
        JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.PLAIN_MESSAGE);
    }
}

class ManagingCyclicBehaviour extends TickerBehaviour {
    ManagerAgent managerAgent;

    public ManagingCyclicBehaviour(ManagerAgent managerAgent) {
        super(managerAgent, 1000);
        this.managerAgent = managerAgent;
    }

    public void onTick() {
        System.out.println("MA");
        ACLMessage message = managerAgent.receive();
        if (message == null) {
            message = new ACLMessage(ACLMessage.REQUEST);
            message.setContent("ManagerAgent");
            message.addReceiver(new AID("ServiceAgent", AID.ISLOCALNAME));
            myAgent.send(message);
            block();
//            return;
        }
        String content = message.getContent();
        int performative = message.getPerformative();
        if (performative == ACLMessage.REQUEST) {
            System.out.println(content);
        }
    }

}
