package models.commands;

import algorithms.SetCoverSolver;
import interfaces.CommandOperation;
import lombok.AllArgsConstructor;
import models.BibliographicReferences;
import models.ConceptRegistry;
import models.RepositoryControl;

@AllArgsConstructor

public class CoverCommand implements CommandOperation {
    private RepositoryControl catalog;

    @Override
    public void execute() {
        System.out.println("=== Set Cover: Minimum Resource Set ===");
        System.out.println("Universe |C| = " + ConceptRegistry.size() + " concepts");
        System.out.println("Catalog  |R| = " + catalog.getReferenceList().size() + " references");
        System.out.println();

        SetCoverSolver.CoverResult result = catalog.cover();

        if (result.isComplete()) {
            System.out.println("COMPLETE cover found with " + result.getCover().size() + " reference(s):");
        } else {
            System.out.println("PARTIAL cover (" + result.getCoveredConcepts().size()
                    + "/" + ConceptRegistry.size() + " concepts) with "
                    + result.getCover().size() + " reference(s):");
        }

        int i = 1;
        for (BibliographicReferences ref : result.getCover()) {
            System.out.printf("  %d. [%s] \"%s\" -- concepts: %s%n",
                    i++, ref.getId(), ref.getTitle(), ref.getConcepts());
        }

        if (!result.getUncoveredConcepts().isEmpty()) {
            System.out.println();
            System.out.println("Uncovered concepts: " + result.getUncoveredConcepts());
            System.out.println("(Add references with these concepts to achieve full coverage.)");
        }

        System.out.printf("%nTime: %.3f ms%n", result.getElapsedMs());
        System.out.println("========================================\n");
    }
}