package repository;

import models.BibliographicReference;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Repository {
    private final Map<String, BibliographicReference> catalog = new LinkedHashMap<>();

    public boolean add(BibliographicReference reference) {
        if (reference == null || reference.getId() == null) {
            throw new IllegalArgumentException("Reference and its id must not be null");
        }
        if (catalog.containsKey(reference.getId())) {
            return false;
        }
        catalog.put(reference.getId(), reference);
        return true;
    }

    public BibliographicReference remove(String id) {
        return catalog.remove(id);
    }

    public BibliographicReference findById(String id) {
        return catalog.get(id);
    }

    public boolean update(BibliographicReference reference) {
        if (reference == null || reference.getId() == null) {
            throw new IllegalArgumentException("Reference and its id must not be null");
        }
        if (!catalog.containsKey(reference.getId())) {
            return false;
        }
        catalog.put(reference.getId(), reference);
        return true;
    }

    public List<BibliographicReference> findByAuthor(String author) {
        List<BibliographicReference> results = new ArrayList<>();
        String lowerAuthor = author.toLowerCase();
        for (BibliographicReference ref : catalog.values()) {
            if (ref.getAuthor() != null && ref.getAuthor().toLowerCase().contains(lowerAuthor)) {
                results.add(ref);
            }
        }
        return results;
    }

    public List<BibliographicReference> findByYear(int year) {
        List<BibliographicReference> results = new ArrayList<>();
        for (BibliographicReference ref : catalog.values()) {
            if (ref.getYear() == year) {
                results.add(ref);
            }
        }
        return results;
    }

    public List<BibliographicReference> findByTitle(String title) {
        List<BibliographicReference> results = new ArrayList<>();
        String lowerTitle = title.toLowerCase();
        for (BibliographicReference ref : catalog.values()) {
            if (ref.getTitle() != null && ref.getTitle().toLowerCase().contains(lowerTitle)) {
                results.add(ref);
            }
        }
        return results;
    }

    public <T extends BibliographicReference> List<T> findByType(Class<T> type) {
        List<T> results = new ArrayList<>();
        for (BibliographicReference ref : catalog.values()) {
            if (type.isInstance(ref)) {
                results.add(type.cast(ref));
            }
        }
        return results;
    }

    public List<BibliographicReference> getAll() {
        return new ArrayList<>(catalog.values());
    }

    public int size() {
        return catalog.size();
    }

    public boolean contains(String id) {
        return catalog.containsKey(id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (BibliographicReference ref : catalog.values()) {
            sb.append(ref.toString()).append(";\n");
        }
        return sb.toString();
    }
}