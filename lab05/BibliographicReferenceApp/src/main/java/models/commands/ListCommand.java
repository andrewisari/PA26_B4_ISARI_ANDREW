package models.commands;

import interfaces.CommandOperation;
import lombok.AllArgsConstructor;
import models.RepositoryControl;

@AllArgsConstructor

public class ListCommand implements CommandOperation {
    private RepositoryControl catalog;

    @Override
    public void execute() {
        catalog.list();
    }
}
