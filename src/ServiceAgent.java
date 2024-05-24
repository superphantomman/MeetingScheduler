package jadelab1;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import java.net.*;
import java.io.*;

public class ServiceAgent extends Agent {
	protected void setup () {
		//services registration at DF
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.setName(getAID());
		//service no 1
		ServiceDescription sd1 = new ServiceDescription();
		sd1.setType("answers");
		sd1.setName("wordnet");

		try {
			DFService.register(this,dfad);
		} catch (FIPAException ex) {
			ex.printStackTrace();
		}
		
		addBehaviour(new WordnetCyclicBehaviour(this));

		//doDelete();
	}
	protected void takeDown() {
		//services deregistration before termination
		try {
			DFService.deregister(this);
		} catch (FIPAException ex) {
			ex.printStackTrace();
		}
	}

}

class WordnetCyclicBehaviour extends TickerBehaviour
{
	ServiceAgent agent;
	public WordnetCyclicBehaviour(ServiceAgent agent)
	{
		super(agent, 5000);
		this.agent = agent;
	}
	public void onTick()
	{
		System.out.println("SA");
		ACLMessage message = agent.receive();
		if (message == null)
		{
			message = new ACLMessage(ACLMessage.REQUEST);
			message.setContent("ServiceAgent");
			message.addReceiver(new AID("ManagerAgent", AID.ISLOCALNAME));
			myAgent.send(message);
			block();
		}
		else
		{
			System.out.println(message.getContent());
			block();
//			ACLMessage reply = message.createReply();
//			reply.setPerformative(ACLMessage.REQUEST);
//			String response = "ServiceAgent";
//			reply.setContent(response);
//			agent.send(reply);
		}
	}
}

