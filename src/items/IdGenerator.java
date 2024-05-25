package jadelab1.items;

public class IdGenerator {
    private static int idMeetingAgentGenerator = 1;
    private static int proposalIdGenerator = 1;


    public static int generateIdMeetingAgent() {
        return idMeetingAgentGenerator++;
    }

    public static int generateIdProposal() {
        return proposalIdGenerator++;
    }
}
