import models.Article;
import models.Book;
import repository.Repository;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) {
        Repository catalog = new Repository();

        catalog.add(new Book("knuth67", "The Art of Computer Programming", "d:/books/programming/tacp.ps", 1967, "Donald E. Knuth", null, "Addison-Wesley", 1));

        catalog.add(new Article("jvm25", "The Java Virtual Machine Specification", "https://docs.oracle.com/javase/specs/jvms/se25/html/index.html", 2025, "Tim Lindholm & others"));

        catalog.add(new Article("java25", "The Java Language Specification", "https://docs.oracle.com/javase/specs/jls/se25/jls25.pdf", 2025, "James Gosling & others"));

        System.out.println(catalog);

        open(catalog.findById("java25").getLocation());
    }

    public static void open(String location) {
        if (!Desktop.isDesktopSupported()) {
            System.err.println("Desktop is not supported on this system");
            return;
        }

        Desktop desktop = Desktop.getDesktop();

        try {
            if (location.startsWith("http://") || location.startsWith("https://")) desktop.browse(new URI(location));
            else desktop.open(new File(location));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Could not open location: " + location);
            e.printStackTrace();
        }
    }
}