package models;

import enums.ResourceType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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

    public BibliographicReferences(String id, String title, String location, int year, String author, ResourceType type) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.year = year;
        this.author = author;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("{\"id\":\"%s\", \"title\":\"%s\", \"location\":\"%s\", \"year\":%d, \"author\":\"%s\", \"type\":\"%s\"}", id, title, location, year, author, type);
    }
}