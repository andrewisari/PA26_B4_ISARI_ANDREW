package models.commands;

import interfaces.CommandOperation;
import lombok.AllArgsConstructor;
import models.BibliographicReferences;
import models.RepositoryControl;

@AllArgsConstructor

public class AddCommand implements CommandOperation {
    private RepositoryControl catalog;
    private BibliographicReferences newReference;

    @Override
    public void execute() {
        catalog.addRef(newReference);
    }
}
