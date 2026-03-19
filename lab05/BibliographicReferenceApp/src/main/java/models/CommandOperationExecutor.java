package models;

import interfaces.CommandOperation;
import models.errors.CatalogException;
import models.errors.FeatureException;
import models.errors.SystemException;

import java.util.ArrayList;
import java.util.List;

public class CommandOperationExecutor {
    private final List<CommandOperation> commandHistory = new ArrayList<>();

    public void executeOperation(CommandOperation commandOperation) {
        try {
            commandOperation.execute();
            commandHistory.add(commandOperation);
        } catch (FeatureException e) {
            System.err.println("[BUSINESS ERROR] " + e.getMessage());
        } catch (SystemException e) {
            System.err.println("[SYSTEM ERROR] " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Caused by: " + e.getCause());
            }
        } catch (CatalogException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    public List<CommandOperation> getCommandHistory() {
        return List.copyOf(commandHistory);
    }
}
