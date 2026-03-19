package models.commands;

import interfaces.CommandOperation;
import lombok.AllArgsConstructor;
import models.RepositoryControl;

@AllArgsConstructor

public class SaveCommand implements CommandOperation {
    private RepositoryControl catalog;
    private String filePath;

    @Override
    public void execute() {
        catalog.save(filePath);
    }
}