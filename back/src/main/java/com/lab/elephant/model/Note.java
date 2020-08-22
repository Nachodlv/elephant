package com.lab.elephant.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "note")
public class Note {

    @Id
    @GeneratedValue
    private long uuid;

    private String title;

    private String content;

    private Timestamp created;

    public Note(String title, String content, Timestamp created) {
        this.title = title;
        this.content = content;
        this.created = created;
    }

    public Note() {
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

}
