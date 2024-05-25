package jadelab1.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO wybrać najlepszą godzinę z podanych, jeśli add da true albo zmienić tak by zwracała null albo godzinne
public class ManagerScheduler {
    private final int numberOfAgents;
    private final List<Offer> offers = new ArrayList<>(24);
    private final Map<Integer, Integer> countPerHour = new HashMap<>(24);

    public ManagerScheduler(int numberOfAgents) {
        for (int i = 0; i < 24; i++) {
            offers.add(new Offer(i, 0));
            countPerHour.put(i, 0);
        }
        this.numberOfAgents = numberOfAgents;
    }

    public boolean add(Offer o){
        offers.add(o);
        offers.sort(Offer::compareTo);
        countPerHour.put(o.hour(), countPerHour.get(o.hour()) + 1);
    return offers.get(0).hour() == o.hour()  && countPerHour.get(o.hour()).equals(numberOfAgents);
    }
}
