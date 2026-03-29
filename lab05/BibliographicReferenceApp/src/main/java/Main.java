import algorithms.SetCoverBenchmark;
import enums.ResourceType;
import models.BibliographicReferences;
import models.CommandOperationExecutor;
import models.ConceptRegistry;
import models.RepositoryControl;
import models.commands.*;

import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private static final String CATALOG_FILE = "catalog.json";

    private static Set<String> promptConcepts(Scanner scanner) {
        System.out.println("Available concepts:");
        ConceptRegistry.printAll();
        System.out.print("Enter concept numbers (comma-separated), or 'none': ");
        String input = scanner.nextLine().trim();

        Set<String> selected = new LinkedHashSet<>();
        if (input.equalsIgnoreCase("none") || input.isEmpty()) {
            return selected;
        }

        java.util.List<String> allConcepts = new java.util.ArrayList<>(ConceptRegistry.getAll());
        for (String token : input.split(",")) {
            try {
                int idx = Integer.parseInt(token.trim()) - 1;
                if (idx >= 0 && idx < allConcepts.size()) {
                    selected.add(allConcepts.get(idx));
                } else {
                    System.out.println("  [WARN] Index out of range: " + (idx + 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("  [WARN] Invalid number: " + token.trim());
            }
        }
        System.out.println("  Selected: " + selected);
        return selected;
    }

    public static void run(RepositoryControl catalog, CommandOperationExecutor executor) {
        System.out.println("=== WELCOME ===");
        System.out.println("Command Examples: ");
        System.out.println(" 1. add       - add a new entry.");
        System.out.println(" 2. list      - prints a list of all references.");
        System.out.println(" 3. load      - prints all details about a specific reference.");
        System.out.println(" 4. remove    - removes target entry from the catalog.");
        System.out.println(" 5. report    - generates an HTML report and opens it in the browser.");
        System.out.println(" 6. save      - saves the current catalog to JSON.");
        System.out.println(" 7. update    - updates information of target entry.");
        System.out.println(" 8. view      - opens an item using the native OS application.");
        System.out.println(" 9. cover     - find minimum set of references covering all concepts in C.");
        System.out.println("10. benchmark - run set cover benchmark with random instances.");
        System.out.println("11. concepts  - list all official concepts in the universe C.");
        System.out.println("12. exit      - terminates the process.");
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

                    Set<String> concepts = promptConcepts(scanner);

                    BibliographicReferences newRef = new BibliographicReferences(
                            id, title, location, year, author, type, concepts);
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

                    Set<String> concepts = promptConcepts(scanner);

                    BibliographicReferences updated = new BibliographicReferences(
                            newId, title, location, year, author, type, concepts);
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

                case "cover": {
                    executor.executeOperation(new CoverCommand(catalog));
                    break;
                }

                case "benchmark": {
                    System.out.println("Running set cover benchmarks...\n");
                    SetCoverBenchmark.main(new String[]{});
                    break;
                }

                case "concepts": {
                    System.out.println("Official Concept Universe C (" + ConceptRegistry.size() + " concepts):");
                    ConceptRegistry.printAll();
                    System.out.println();
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
                    System.out.println("Valid commands: add, list, load, remove, report, save, "
                            + "update, view, cover, benchmark, concepts, exit\n");
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
                    "Donald E. Knuth", ResourceType.Book,
                    new LinkedHashSet<>(java.util.List.of(
                            "Algorithm design techniques", "Data structures",
                            "Graph theory", "Computational complexity")));

            BibliographicReferences jvm = new BibliographicReferences("jvm25",
                    "The Java Virtual Machine Specification",
                    "https://docs.oracle.com/javase/specs/jvms/se25/html/index.html", 2025,
                    "Tim Lindholm & others", ResourceType.Article,
                    new LinkedHashSet<>(java.util.List.of(
                            "Compiler design", "Software engineering",
                            "Object-oriented programming")));

            BibliographicReferences jls = new BibliographicReferences("java25",
                    "The Java Language Specification",
                    "https://docs.oracle.com/javase/specs/jls/se25/jls25.pdf", 2025,
                    "James Gosling & others", ResourceType.Article,
                    new LinkedHashSet<>(java.util.List.of(
                            "Formal languages", "Object-oriented programming",
                            "Software engineering")));

            executor.executeOperation(new AddCommand(catalog, knuth));
            executor.executeOperation(new AddCommand(catalog, jvm));
            executor.executeOperation(new AddCommand(catalog, jls));
        }

        run(catalog, executor);
    }
}