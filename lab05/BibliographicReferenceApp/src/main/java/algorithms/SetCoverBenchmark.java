package algorithms;

import enums.ResourceType;
import models.BibliographicReferences;
import models.ConceptRegistry;

import java.util.*;

public final class SetCoverBenchmark {

    private static final ResourceType[] TYPES = ResourceType.values();
    private static final Random RNG = new Random();

    private SetCoverBenchmark() {}

    public static List<BibliographicReferences> generateInstance(
            int numRefs, int minConcepts, int maxConcepts, Long seed) {

        if (seed != null) RNG.setSeed(seed);

        List<String> allConcepts = new ArrayList<>(ConceptRegistry.getAll());
        int universeSize = allConcepts.size();

        if (minConcepts < 1) minConcepts = 1;
        if (maxConcepts > universeSize) maxConcepts = universeSize;
        if (minConcepts > maxConcepts) minConcepts = maxConcepts;

        List<BibliographicReferences> refs = new ArrayList<>();

        List<String> shuffled = new ArrayList<>(allConcepts);
        Collections.shuffle(shuffled, RNG);
        int cursor = 0;

        for (int i = 0; i < numRefs; i++) {
            int conceptCount = minConcepts + RNG.nextInt(maxConcepts - minConcepts + 1);
            Set<String> chosen = new LinkedHashSet<>();

            if (cursor < shuffled.size()) {
                int take = Math.min(conceptCount, shuffled.size() - cursor);
                for (int j = 0; j < take; j++) {
                    chosen.add(shuffled.get(cursor++));
                }
            }

            while (chosen.size() < conceptCount) {
                chosen.add(allConcepts.get(RNG.nextInt(universeSize)));
            }

            String id = "ref_" + String.format("%04d", i + 1);
            String title = "Random Reference " + (i + 1);
            String author = "Author_" + (char) ('A' + RNG.nextInt(26));
            int year = 2000 + RNG.nextInt(26);
            ResourceType type = TYPES[RNG.nextInt(TYPES.length)];

            refs.add(new BibliographicReferences(
                    id, title, "https://example.com/" + id, year, author, type, chosen));
        }

        Set<String> covered = new LinkedHashSet<>();
        for (BibliographicReferences r : refs) {
            covered.addAll(r.getConcepts());
        }
        if (!covered.containsAll(ConceptRegistry.getAll())) {
            throw new IllegalStateException("BUG: generated instance does not cover all concepts");
        }

        return refs;
    }


    public static void runComparison(List<BibliographicReferences> refs, String label) {
        Set<String> universe = ConceptRegistry.getAll();

        System.out.println("================================================================");
        System.out.println("  BENCHMARK: " + label);
        System.out.println("  Universe |C| = " + universe.size()
                + "   References |R| = " + refs.size());
        System.out.println("================================================================");

        SetCoverSolver.CoverResult greedy = SetCoverSolver.greedy(refs, universe);
        greedy.print("Greedy Approximation");

        if (refs.size() <= SetCoverSolver.MAX_EXACT_SIZE) {
            SetCoverSolver.CoverResult exact = SetCoverSolver.exact(refs, universe);
            exact.print("Exact (Brute-Force)");

            System.out.println("--- Comparison ---");
            System.out.printf("Greedy size: %d   |   Exact size: %d   |   Ratio: %.2f%n",
                    greedy.getCover().size(),
                    exact.getCover().size(),
                    (double) greedy.getCover().size() / Math.max(1, exact.getCover().size()));
            System.out.printf("Greedy time: %.3f ms   |   Exact time: %.3f ms   |   Speedup: %.1fx%n",
                    greedy.getElapsedMs(),
                    exact.getElapsedMs(),
                    exact.getElapsedMs() / Math.max(0.001, greedy.getElapsedMs()));
        } else {
            System.out.println("[INFO] Exact solver skipped (n=" + refs.size()
                    + " exceeds cap of " + SetCoverSolver.MAX_EXACT_SIZE + ").");
            System.out.println("Greedy solution size: " + greedy.getCover().size());
            System.out.printf("Greedy time: %.3f ms%n", greedy.getElapsedMs());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("Set Cover Benchmark");
        System.out.println("Universe C (" + ConceptRegistry.size() + " concepts):");
        ConceptRegistry.printAll();
        System.out.println();

        List<BibliographicReferences> small =
                generateInstance(8, 2, 5, 42L);
        runComparison(small, "Small instance (8 refs, 2-5 concepts each)");

        List<BibliographicReferences> medium =
                generateInstance(15, 1, 4, 123L);
        runComparison(medium, "Medium instance (15 refs, 1-4 concepts each)");

        List<BibliographicReferences> larger =
                generateInstance(20, 2, 6, 999L);
        runComparison(larger, "Larger instance (20 refs, 2-6 concepts each)");

        List<BibliographicReferences> big =
                generateInstance(50, 1, 3, 7777L);
        runComparison(big, "Large instance (50 refs, 1-3 concepts each, greedy only)");

        List<BibliographicReferences> stress =
                generateInstance(500, 1, 4, 31415L);
        runComparison(stress, "Stress test (500 refs, 1-4 concepts each, greedy only)");

        List<BibliographicReferences> dense =
                generateInstance(10, 5, 10, 2025L);
        runComparison(dense, "Dense instance (10 refs, 5-10 concepts each)");

        System.out.println("Benchmark complete.");
    }
}