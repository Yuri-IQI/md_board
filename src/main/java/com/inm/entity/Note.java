package com.inm.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "notes")
public class Note extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String username;

    @Column(columnDefinition = "TEXT")
    public String markdown;

    @Column(columnDefinition = "TEXT")
    public String renderedHtml;

    public static Note findByUsername(String username) {
        return find("username", username).firstResult();
    }

    public static List<Note> findOthers(String username) {
        return list("username != ?1", username);
    }
}