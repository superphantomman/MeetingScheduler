package jadelab1.items;

import java.util.LinkedList;
import java.util.Random;

import static java.lang.Math.abs;

public class OffersGenerator {

    private static final Random random = new Random();

    public static LinkedList<Offer> generateOffers() {
        final LinkedList<Offer> offers = new LinkedList<>();
        for (int i = 0; i < 23; i++) {
            offers.add(generateOffer(i));
        }
        offers.sort(Offer::compareTo);
        return offers;
    }

    public static Offer generateOffer(int hour) {
        return new Offer(hour, abs((float) (random.nextInt() % 11)) / 10);
    }
}
