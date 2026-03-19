package models.commands;

import interfaces.CommandOperation;
import lombok.AllArgsConstructor;
import models.BibliographicReferences;
import models.RepositoryControl;

import java.util.Set;

@AllArgsConstructor

public class ReportCommand implements CommandOperation {
    private RepositoryControl catalog;

    @Override
    public void execute() {
        catalog.report();
    }
}
