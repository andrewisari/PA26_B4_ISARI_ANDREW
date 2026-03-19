package models.commands;

import interfaces.CommandOperation;
import lombok.AllArgsConstructor;
import models.BibliographicReferences;
import models.RepositoryControl;

@AllArgsConstructor

public class RemoveCommand implements CommandOperation {
    private RepositoryControl catalog;
    private BibliographicReferences targetReference;

    @Override
    public void execute() {
        catalog.removeRef(targetReference);
    }
}
