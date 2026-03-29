package models;

import algorithms.SetCoverSolver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.errors.features.DuplicateException;
import models.errors.features.NotFoundException;
import models.errors.systems.AccessException;
import models.errors.systems.ReportException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
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

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type REF_SET_TYPE = new TypeToken<HashSet<BibliographicReferences>>() {}.getType();

    public void loadFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[INFO] No catalog file found at: " + filePath + " -- starting with empty catalog.");
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Set<BibliographicReferences> loaded = GSON.fromJson(reader, REF_SET_TYPE);
            if (loaded != null) {
                referenceList.addAll(loaded);
            }
            System.out.println("[INFO] Loaded " + referenceList.size() + " reference(s) from: " + filePath);
        } catch (IOException e) {
            throw new ReportException("Failed to read catalog from JSON file: " + filePath, e);
        }
    }

    public void save(String filePath) {
        try (Writer writer = new FileWriter(filePath)) {
            GSON.toJson(referenceList, writer);
            System.out.println("[INFO] Saved " + referenceList.size() + " reference(s) to: " + filePath);
        } catch (IOException e) {
            throw new ReportException("Failed to write catalog to JSON file: " + filePath, e);
        }
    }

    public void addRef(BibliographicReferences newReference) {
        if (!referenceList.add(newReference)) {
            throw new DuplicateException(newReference.getId());
        }
    }

    public void removeRef(BibliographicReferences referenceToRemove) {
        if (!referenceList.remove(referenceToRemove)) {
            throw new NotFoundException(referenceToRemove.getId());
        }
    }

    public void updateAll(BibliographicReferences targetReference, BibliographicReferences updateReference) {
        removeRef(targetReference);
        addRef(updateReference);
    }

    public void load(BibliographicReferences targetReference) {
        BibliographicReferences ref = findOrThrow(targetReference);

        System.out.println("=== Reference Details ===");
        System.out.println("ID:       " + ref.getId());
        System.out.println("Title:    " + ref.getTitle());
        System.out.println("Author:   " + ref.getAuthor());
        System.out.println("Year:     " + ref.getYear());
        System.out.println("Type:     " + ref.getType());
        System.out.println("Location: " + ref.getLocation());
        System.out.println("Concepts: " + ref.getConcepts());
        System.out.println("=========================\n\n");
    }

    public void list() {
        int i = 1;
        for (BibliographicReferences ref : referenceList) {
            System.out.println("[ENTRY: " + i + "] " + ref);
            i++;
        }
    }

    public void view(BibliographicReferences targetReference) {
        BibliographicReferences ref = findOrThrow(targetReference);
        openLocation(ref.getLocation());
    }

    public SetCoverSolver.CoverResult cover() {
        Set<String> universe = ConceptRegistry.getAll();
        SetCoverSolver.CoverResult result = SetCoverSolver.greedy(referenceList, universe);
        return result;
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
            throw new ReportException("Failed to create or write the HTML report file.", e);
        }
    }

    private BibliographicReferences findOrThrow(BibliographicReferences target) {
        return referenceList.stream()
                .filter(ref -> ref.equals(target))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(target.getId()));
    }

    private void openLocation(String location) {
        if (!Desktop.isDesktopSupported()) {
            throw new AccessException(location, "Desktop API is not supported on this system");
        }

        Desktop desktop = Desktop.getDesktop();

        try {
            if (location.startsWith("http://") || location.startsWith("https://")) {
                desktop.browse(new URI(location));
            } else {
                desktop.open(new File(location));
            }
        } catch (IOException | URISyntaxException e) {
            throw new AccessException(location, e);
        }
    }
}