package models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class RepositoryControl {
    @EqualsAndHashCode.Include
    private Set<BibliographicReferences> referenceList = new HashSet<>();

    public void addRef(BibliographicReferences newReference) {
        if (!referenceList.add(newReference)) throw new RuntimeException("[ERROR] Could not add this reference. Please try again. [ERROR]");
    }

    public void removeRef(BibliographicReferences referenceToRemove) {
        if (!referenceList.contains(referenceToRemove)) throw new RuntimeException("[ERROR] Reference you want to remove does not exist. [ERROR]");
        if (!referenceList.remove(referenceToRemove)) throw new RuntimeException("[ERROR] Could not remove this reference. Please try again. [ERROR]");
    }

    public void updateAll(BibliographicReferences targetReference, BibliographicReferences updateReference) {
        if (!referenceList.contains(targetReference)) throw new RuntimeException("[ERROR] Target reference does not exist. [ERROR]");
        removeRef(targetReference);
        addRef(updateReference);
    }

    public void load(BibliographicReferences targetReference) {
        for (BibliographicReferences ref : referenceList) {
            if (ref.equals(targetReference)) {
                System.out.println("=== Reference Details ===");
                System.out.println("ID:       " + ref.getId());
                System.out.println("Title:    " + ref.getTitle());
                System.out.println("Author:   " + ref.getAuthor());
                System.out.println("Year:     " + ref.getYear());
                System.out.println("Type:     " + ref.getType());
                System.out.println("Location: " + ref.getLocation());
                System.out.println("=========================");
                return;
            }
        }
        throw new RuntimeException("[ERROR] Reference not found in catalog. [ERROR]");
    }

    public void list() {
        int i = 1;
        for (BibliographicReferences ref : referenceList) {
            System.out.println("[ENTRY: " + i + "] " + ref);
            i++;
        }
    }

    public void view(BibliographicReferences targetReference) {
        for (BibliographicReferences ref : referenceList) {
            if (ref.equals(targetReference)) {
                openLocation(ref.getLocation());
                return;
            }
        }
        throw new RuntimeException("[ERROR] Reference not found in catalog. [ERROR]");
    }

    public void report() {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("resource.loaders", "classpath");
        engine.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        engine.init();

        Template template = engine.getTemplate("templates/report.vm");

        VelocityContext context = new VelocityContext();
        context.put("references", referenceList);
        context.put("totalCount", referenceList.size());

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        try {
            File reportFile = File.createTempFile("catalog_report_", ".html");
            try (FileWriter fileWriter = new FileWriter(reportFile)) {
                fileWriter.write(writer.toString());
            }
            openLocation(reportFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("[ERROR] Could not create or open the report file. [ERROR]", e);
        }
    }

    private void openLocation(String location) {
        if (!Desktop.isDesktopSupported()) throw new RuntimeException("[ERROR] Desktop is not supported on this system. [ERROR]");

        Desktop desktop = Desktop.getDesktop();

        try {
            if (location.startsWith("http://") || location.startsWith("https://")) {
                desktop.browse(new URI(location));
            } else {
                desktop.open(new File(location));
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("[ERROR] Could not open target location. [ERROR]", e);
        }
    }
}