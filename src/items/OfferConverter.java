package jadelab1.items;

import java.util.Arrays;
import java.util.List;

public class OfferConverter {

    public static List<Offer> convert(String content) {
        return Arrays.stream(content.split(";")).map(Offer::new).toList();
    }

    public static String convert(List<Offer> offers) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Offer offer : offers) {
            stringBuilder.append(offer.toString()).append(";");
        }

        return stringBuilder.toString();
    }
}
