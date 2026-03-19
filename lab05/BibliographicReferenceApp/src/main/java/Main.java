import enums.ResourceType;
import models.BibliographicReferences;
import models.RepositoryControl;

public class Main {
    public static void main(String[] args) {
        RepositoryControl catalog = new RepositoryControl();

        catalog.addRef(new BibliographicReferences("knuth67", "The Art of Computer Programming",
                "d:/books/programming/tacp.ps", 1967, "Donald E. Knuth", ResourceType.Book));

        catalog.addRef(new BibliographicReferences("jvm25", "The Java Virtual Machine Specification", "https://docs.oracle.com/javase/specs/jvms/se25/html/index.html", 2025, "Tim Lindholm & others", ResourceType.Article));

        catalog.addRef(new BibliographicReferences("java25", "The Java Language Specification", "https://docs.oracle.com/javase/specs/jls/se25/jls25.pdf", 2025, "James Gosling & others", ResourceType.Article));


    }
}