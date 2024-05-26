package jadelab1.items;

import java.util.*;

public class ManagerScheduler {
    private final int numberOfAgents;
    private final List<Offer> offers = new ArrayList<>(24);
    private final Map<Integer, Integer> countPerHour = new HashMap<>(24);

    private final Set<String> completedTransactions;

    public ManagerScheduler(int numberOfAgents) {
        for (int i = 0; i < 24; i++) {
            offers.add(new Offer(i, 0));
            countPerHour.put(i, 0);
        }
        this.numberOfAgents = numberOfAgents;
        completedTransactions = new HashSet<>(numberOfAgents);
    }

    public boolean add(String id, List<Offer> currentOffers) {
        if (completedTransactions.contains(id)) {
            return false;
        }

        for (Offer o1 : currentOffers) {
            for (Offer o2 : offers) {
                if (o1.hour() == o2.hour()) {
                    o2.setPriority(o2.priority() + o1.priority());
                }
            }

            countPerHour.put(o1.hour(), countPerHour.get(o1.hour()) + 1);

        }

        offers.sort(Offer::compareTo);
        completedTransactions.add(id);
        return countPerHour.get(offers.get(0).hour()).equals(numberOfAgents);
    }
    public int getBestMeetingTime() {
        return offers.get(0).hour();
    }

    public int getNumberOfAgents() {
        return numberOfAgents;
    }
}
