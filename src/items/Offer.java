package jadelab1.items;

public record Offer(int hour, float priority) implements Comparable<Offer> {

    public Offer {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Hour must be between 0 and 23");
        }
        if (priority < 0) {
            throw new IllegalArgumentException("Priority must be non-negative");
        }
    }

    public Offer(String string) {
        this(parseHour(string), parsePriority(string));
    }

    private static int parseHour(String string) {
        String[] parts = string.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid input format: " + string);
        }
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hour value: " + parts[0]);
        }
    }

    private static float parsePriority(String string) {
        String[] parts = string.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid input format: " + string);
        }
        try {
            return Float.parseFloat(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid priority value: " + parts[1]);
        }
    }

    @Override
    public int compareTo(Offer other) {
        int priorityComparison = Float.compare(this.priority, other.priority);
        if (priorityComparison != 0) {
            return -priorityComparison; // Higher priority first
        }
        return Integer.compare(this.hour, other.hour); // Earlier hour first
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Offer offer)) return false;

        return hour == offer.hour && Float.compare(priority, offer.priority) == 0;
    }

    @Override
    public int hashCode() {
        int result = hour;
        result = 31 * result + Float.hashCode(priority);
        return result;
    }

    @Override
    public String toString() {
        return hour + "-" + priority;
    }
}
