package jadelab1.agents;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jadelab1.items.OfferConverter;
import jadelab1.items.Offer;

import java.util.LinkedList;

import static jadelab1.items.IdGenerator.*;
import static jadelab1.items.OffersGenerator.generateOffers;

public class MeetingAgent extends Agent {
    private Integer id;


    protected void setup() {
        //assign id to agent
        id = generateIdMeetingAgent();
        //services registration at DF
        DFAgentDescription dfad = new DFAgentDescription();
        dfad.setName(getAID());
        //service no 1
        ServiceDescription sd = new ServiceDescription();
        sd.setType("answers");
        sd.setName("meeting");

        try {
            DFService.register(this, dfad);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }
        final IntroduceYourselfOneShotBehaviour introduceYourselfBehaviour = new IntroduceYourselfOneShotBehaviour(this);
        final MeetingTickerBehaviour meetingTickerBehaviour = new MeetingTickerBehaviour(this);


        WakerBehaviour wakerBehaviour = new WakerBehaviour(this, 5000) {
            @Override
            protected void onWake() {

                addBehaviour(meetingTickerBehaviour);
            }
        };
        addBehaviour(wakerBehaviour);
        addBehaviour(introduceYourselfBehaviour);

    }

    protected void takeDown() {
        //services deregistration before termination
        try {
            DFService.deregister(this);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }
    }

    public Integer getId() {
        return id;
    }
}

class IntroduceYourselfOneShotBehaviour extends OneShotBehaviour {
    private final MeetingAgent agent;

    public IntroduceYourselfOneShotBehaviour(MeetingAgent agent) {
        super(agent);
        this.agent = agent;
    }

    @Override
    public void action() {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("ManagerAgent", AID.ISLOCALNAME));
        message.setContent("introduce");
        agent.send(message);
    }

}

class MeetingTickerBehaviour extends TickerBehaviour {
    private final MeetingAgent agent;
    private final LinkedList<Offer> offers;
    private boolean isConversationOver = false;
    private final LinkedList<Offer> bestOffers = new LinkedList<>();
    private int currentOfferId = generateIdProposal();

    public MeetingTickerBehaviour(MeetingAgent agent) {
        super(agent, 2000);
        offers = generateOffers();
        updateBestOffers();
        System.out.println("SA offers" + offers);
        this.agent = agent;
    }

    public LinkedList<Offer> bestOffers() {
        return bestOffers;
    }

    public void updateBestOffers() {
        bestOffers.clear();
        Offer bestOffer = offers.get(0);
        int i = 0;
        while (i < offers.size() && offers.get(i).priority() == bestOffer.priority()) {
            bestOffers.add(offers.get(i));
            offers.remove(i);
        }
    }


    public void onTick() {


        if(bestOffers.isEmpty() && offers.isEmpty()) {
            isConversationOver = true;
            ACLMessage message =  createEndMessage();
            agent.send(message);
            stop();
            return;
        }

        ACLMessage message = agent.receive();
        if (message != null && message.getPerformative() == ACLMessage.INFORM && message.getContent().equals("End")) {
            isConversationOver = true;
            stop();
            return;
        }

        sendingProposal(message);

    }

    private static void delay(long seconds) {
        try {
            // Wait for an additional 1 second
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ACLMessage createEndMessage() {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("ManagerAgent", AID.ISLOCALNAME));
        message.setContent("end");
        return message;
    }

    private void sendingProposal(ACLMessage message) {

        if (message == null) {
            message = createProposalMessage();
            agent.send(message);
            delay(1L);
        } else {
            final int performative = message.getPerformative();
            if (performative == ACLMessage.ACCEPT_PROPOSAL) {
                currentOfferId = generateIdProposal();
                updateBestOffers();
            }
        }
    }

    private ACLMessage createProposalMessage() {
        ACLMessage message;
        message = new ACLMessage(ACLMessage.PROPOSE);
        message.setContent(OfferConverter.convert(bestOffers()));
        message.setConversationId(String.valueOf(currentOfferId));
        message.addReceiver(new AID("ManagerAgent", AID.ISLOCALNAME));
        return message;
    }
}

