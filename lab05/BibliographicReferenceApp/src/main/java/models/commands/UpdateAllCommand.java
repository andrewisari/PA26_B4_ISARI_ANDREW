package models.commands;

import interfaces.CommandOperation;
import models.BibliographicReferences;
import models.RepositoryControl;

public class UpdateAllCommand implements CommandOperation {
    private RepositoryControl catalog;
    private BibliographicReferences targetReference;
    private BibliographicReferences newReference;

    @Override
    public void execute() {
        catalog.updateAll(targetReference, newReference);
    }
}
