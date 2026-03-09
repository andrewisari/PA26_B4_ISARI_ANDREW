import java.util.Objects;

public class Street implements Comparable<Street> {

    private final String name;
    private final double length;
    private final Intersection start;
    private final Intersection end;

    public Street(String name, double length, Intersection start, Intersection end) {
        this.name = name;
        this.length = length;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        return length;
    }

    public Intersection getStart() {
        return start;
    }

    public Intersection getEnd() {
        return end;
    }

    @Override
    public int compareTo(Street other) {
        return Double.compare(this.length, other.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Street)) return false;
        Street that = (Street) o;
        return Double.compare(length, that.length) == 0
                && Objects.equals(name, that.name)
                && Objects.equals(start, that.start)
                && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, length, start, end);
    }

    @Override
    public String toString() {
        return String.format("Street(%-18s %6.1f m  [%s <-> %s])",
                "\"" + name + "\"", length, start.getName(), end.getName());
    }
}