import enums.ResourceType;
import models.BibliographicReferences;
import models.CommandOperationExecutor;
import models.RepositoryControl;
import models.commands.*;

import java.util.Scanner;

public class Main {

    private static final String CATALOG_FILE = "catalog.json";

    public static void run(RepositoryControl catalog, CommandOperationExecutor executor) {
        System.out.println("=== WELCOME ===");
        System.out.println("Command Examples: ");
        System.out.println("1. add    - to add a new entry.");
        System.out.println("2. list   - prints a list of all references to the screen.");
        System.out.println("3. load   - prints all details about a specific reference.");
        System.out.println("4. remove - removes target entry from the catalog.");
        System.out.println("5. report - generates a report with all the entries and opens it in your default browser.");
        System.out.println("6. save   - saves the current version of the JSON file.");
        System.out.println("7. update - updates information of target entry with new information.");
        System.out.println("8. view   - opens an item using the native operating system application.");
        System.out.println("9. exit   - terminates the process.");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Input command: ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "add": {
                    System.out.print("ID: ");
                    String id = scanner.nextLine().trim();

                    System.out.print("Title: ");
                    String title = scanner.nextLine().trim();

                    System.out.print("Author: ");
                    String author = scanner.nextLine().trim();

                    System.out.print("Year: ");
                    int year = Integer.parseInt(scanner.nextLine().trim());

                    System.out.print("Location (file path or URL): ");
                    String location = scanner.nextLine().trim();

                    System.out.print("Type (Book, Article, Thesis, Report, Website, Other): ");
                    ResourceType type = ResourceType.valueOf(scanner.nextLine().trim());

                    BibliographicReferences newRef = new BibliographicReferences(
                            id, title, location, year, author, type);
                    executor.executeOperation(new AddCommand(catalog, newRef));
                    System.out.println("Reference added.\n");
                    break;
                }

                case "list": {
                    executor.executeOperation(new ListCommand(catalog));
                    System.out.println();
                    break;
                }

                case "load": {
                    System.out.print("Enter reference ID: ");
                    String id = scanner.nextLine().trim();
                    BibliographicReferences lookup = new BibliographicReferences(
                            id, null, null, 0, null, null);
                    executor.executeOperation(new LoadCommand(catalog, lookup));
                    break;
                }

                case "remove": {
                    System.out.print("Enter reference ID to remove: ");
                    String id = scanner.nextLine().trim();
                    BibliographicReferences lookup = new BibliographicReferences(
                            id, null, null, 0, null, null);
                    executor.executeOperation(new RemoveCommand(catalog, lookup));
                    System.out.println("Reference removed.\n");
                    break;
                }

                case "report": {
                    executor.executeOperation(new ReportCommand(catalog));
                    break;
                }

                case "save": {
                    executor.executeOperation(new SaveCommand(catalog, CATALOG_FILE));
                    break;
                }

                case "update": {
                    System.out.print("Enter ID of reference to update: ");
                    String targetId = scanner.nextLine().trim();
                    BibliographicReferences target = new BibliographicReferences(
                            targetId, null, null, 0, null, null);

                    System.out.println("Enter new values for this reference:");

                    System.out.print("New ID (or same): ");
                    String newId = scanner.nextLine().trim();

                    System.out.print("New Title: ");
                    String title = scanner.nextLine().trim();

                    System.out.print("New Author: ");
                    String author = scanner.nextLine().trim();

                    System.out.print("New Year: ");
                    int year = Integer.parseInt(scanner.nextLine().trim());

                    System.out.print("New Location: ");
                    String location = scanner.nextLine().trim();

                    System.out.print("New Type (Book, Article, Thesis, Report, Website, Other): ");
                    ResourceType type = ResourceType.valueOf(scanner.nextLine().trim());

                    BibliographicReferences updated = new BibliographicReferences(
                            newId, title, location, year, author, type);
                    executor.executeOperation(new UpdateAllCommand(catalog, target, updated));
                    System.out.println("Reference updated.\n");
                    break;
                }

                case "view": {
                    System.out.print("Enter reference ID to view: ");
                    String id = scanner.nextLine().trim();
                    BibliographicReferences lookup = new BibliographicReferences(
                            id, null, null, 0, null, null);
                    executor.executeOperation(new ViewCommand(catalog, lookup));
                    break;
                }

                case "exit": {
                    System.out.println("Saving and exiting...");
                    executor.executeOperation(new SaveCommand(catalog, CATALOG_FILE));
                    scanner.close();
                    return;
                }

                default: {
                    System.out.println("Unknown command: " + command);
                    System.out.println("Valid commands: add, list, load, remove, report, save, update, view, exit\n");
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        RepositoryControl catalog = new RepositoryControl();
        CommandOperationExecutor executor = new CommandOperationExecutor();

        catalog.loadFromFile(CATALOG_FILE);

        if (catalog.getReferenceList().isEmpty()) {
            BibliographicReferences knuth = new BibliographicReferences("knuth67",
                    "The Art of Computer Programming",
                    "d:/books/programming/tacp.ps", 1967,
                    "Donald E. Knuth", ResourceType.Book);

            BibliographicReferences jvm = new BibliographicReferences("jvm25",
                    "The Java Virtual Machine Specification",
                    "https://docs.oracle.com/javase/specs/jvms/se25/html/index.html", 2025,
                    "Tim Lindholm & others", ResourceType.Article);

            BibliographicReferences jls = new BibliographicReferences("java25",
                    "The Java Language Specification",
                    "https://docs.oracle.com/javase/specs/jls/se25/jls25.pdf", 2025,
                    "James Gosling & others", ResourceType.Article);

            executor.executeOperation(new AddCommand(catalog, knuth));
            executor.executeOperation(new AddCommand(catalog, jvm));
            executor.executeOperation(new AddCommand(catalog, jls));
        }

        run(catalog, executor);
    }
}