import lombok.Getter;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class SpanningTreeSolution implements Comparable<SpanningTreeSolution> {

    private final Set<Street> streets;
    private final double totalCost;

    public SpanningTreeSolution(Set<Street> streets) {
        this.streets = streets;
        this.totalCost = streets.stream().mapToDouble(Street::getLength).sum();
    }

    @Override
    public int compareTo(SpanningTreeSolution other) {
        return Double.compare(this.totalCost, other.totalCost);
    }

    @Override
    public String toString() {
        String streetList = streets.stream()
                .sorted(Comparator.comparingDouble(Street::getLength))
                .map(s -> "    " + s)
                .collect(Collectors.joining("\n"));
        return String.format("SpanningTreeSolution(cost=%.1f)\n%s", totalCost, streetList);
    }
}