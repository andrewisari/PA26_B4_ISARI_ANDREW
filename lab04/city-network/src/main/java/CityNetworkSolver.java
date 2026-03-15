import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class CityNetworkSolver {

    private final City city;
    private final SimpleWeightedGraph<Intersection, DefaultWeightedEdge> graph;
    private final Map<DefaultWeightedEdge, Street> edgeToStreet;

    public CityNetworkSolver(City city) {
        this.city = city;
        this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        this.edgeToStreet = new HashMap<>();
        buildGraph();
    }

    private void buildGraph() {
        city.getIntersections().forEach(graph::addVertex);
        for (Street street : city.getStreets()) {
            if (street.getStart().equals(street.getEnd())) continue;
            if (graph.containsEdge(street.getStart(), street.getEnd())) continue;
            DefaultWeightedEdge e = graph.addEdge(street.getStart(), street.getEnd());
            graph.setEdgeWeight(e, street.getLength());
            edgeToStreet.put(e, street);
        }
    }

    public List<SpanningTreeSolution> findKBestSpanningTrees(int k) {
        List<SpanningTreeSolution> results = new ArrayList<>();
        PriorityQueue<SpanningTreeSolution> queue = new PriorityQueue<>();
        Set<Set<Street>> visited = new HashSet<>();

        SpanningTreeSolution mst = computeMST();
        queue.add(mst);
        visited.add(mst.getStreets());

        while (!queue.isEmpty() && results.size() < k) {
            SpanningTreeSolution current = queue.poll();
            results.add(current);

            Set<Street> nonTreeStreets = city.getStreets().stream()
                    .filter(s -> !s.getStart().equals(s.getEnd()))
                    .filter(s -> !current.getStreets().contains(s))
                    .collect(Collectors.toSet());

            for (Street nonTreeEdge : nonTreeStreets) {
                List<Street> cyclePath = findPathInTree(
                        current.getStreets(),
                        nonTreeEdge.getStart(),
                        nonTreeEdge.getEnd()
                );

                if (cyclePath == null) continue;

                for (Street treeEdge : cyclePath) {
                    Set<Street> candidate = new HashSet<>(current.getStreets());
                    candidate.remove(treeEdge);
                    candidate.add(nonTreeEdge);

                    if (visited.add(candidate)) {
                        queue.add(new SpanningTreeSolution(candidate));
                    }
                }
            }
        }

        return results;
    }

    private SpanningTreeSolution computeMST() {
        KruskalMinimumSpanningTree<Intersection, DefaultWeightedEdge> kruskal =
                new KruskalMinimumSpanningTree<>(graph);

        Set<Street> mstStreets = kruskal.getSpanningTree().getEdges().stream()
                .map(edgeToStreet::get)
                .collect(Collectors.toSet());

        return new SpanningTreeSolution(mstStreets);
    }

    private List<Street> findPathInTree(Set<Street> tree, Intersection source, Intersection target) {
        if (source.equals(target)) return Collections.emptyList();

        Map<Intersection, Street> parentEdge = new HashMap<>();
        Queue<Intersection> bfsQueue = new LinkedList<>();
        Set<Intersection> visited = new HashSet<>();

        bfsQueue.add(source);
        visited.add(source);

        outer:
        while (!bfsQueue.isEmpty()) {
            Intersection current = bfsQueue.poll();
            for (Street street : tree) {
                Intersection neighbor = null;
                if (street.getStart().equals(current) && !visited.contains(street.getEnd())) {
                    neighbor = street.getEnd();
                } else if (street.getEnd().equals(current) && !visited.contains(street.getStart())) {
                    neighbor = street.getStart();
                }
                if (neighbor != null) {
                    parentEdge.put(neighbor, street);
                    if (neighbor.equals(target)) break outer;
                    visited.add(neighbor);
                    bfsQueue.add(neighbor);
                }
            }
        }

        if (!parentEdge.containsKey(target)) return null;

        List<Street> path = new ArrayList<>();
        Intersection curr = target;
        while (!curr.equals(source)) {
            Street edge = parentEdge.get(curr);
            path.add(edge);
            curr = edge.getStart().equals(curr) ? edge.getEnd() : edge.getStart();
        }
        return path;
    }
}