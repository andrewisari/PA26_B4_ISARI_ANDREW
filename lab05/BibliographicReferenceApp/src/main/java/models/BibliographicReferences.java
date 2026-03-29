package models;

import enums.ResourceType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class BibliographicReferences {
    @EqualsAndHashCode.Include
    private String id;

    private String title;
    private String location;
    private int year;
    private String author;
    private ResourceType type;
    private Set<String> concepts;

    public BibliographicReferences(String id, String title, String location,
                                   int year, String author, ResourceType type) {
        this(id, title, location, year, author, type, new LinkedHashSet<>());
    }

    public BibliographicReferences(String id, String title, String location,
                                   int year, String author, ResourceType type,
                                   Set<String> concepts) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.year = year;
        this.author = author;
        this.type = type;
        this.concepts = (concepts != null) ? new LinkedHashSet<>(concepts) : new LinkedHashSet<>();
    }

    public Set<String> getConceptsView() {
        return Collections.unmodifiableSet(concepts);
    }

    public void addConcept(String concept) {
        concepts.add(concept);
    }

    public void removeConcept(String concept) {
        concepts.remove(concept);
    }

    @Override
    public String toString() {
        return String.format(
                "{\"id\":\"%s\", \"title\":\"%s\", \"location\":\"%s\", \"year\":%d, " +
                        "\"author\":\"%s\", \"type\":\"%s\", \"concepts\":%s}",
                id, title, location, year, author, type, concepts);
    }
}