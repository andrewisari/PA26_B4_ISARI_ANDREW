import enums.ResourceType;
import models.BibliographicReferences;
import models.CommandOperationExecutor;
import models.RepositoryControl;
import models.commands.*;

public class Main {

    private static final String CATALOG_FILE = "catalog.json";

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

        executor.executeOperation(new ListCommand(catalog));

        BibliographicReferences jvmLookup = new BibliographicReferences("jvm25", null, null, 0, null, null);
        BibliographicReferences jlsLookup = new BibliographicReferences("java25", null, null, 0, null, null);

        executor.executeOperation(new LoadCommand(catalog, jvmLookup));
        executor.executeOperation(new ViewCommand(catalog, jlsLookup));
        executor.executeOperation(new ReportCommand(catalog));

        executor.executeOperation(new SaveCommand(catalog, CATALOG_FILE));
    }
}