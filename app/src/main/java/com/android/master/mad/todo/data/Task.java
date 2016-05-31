package com.android.master.mad.todo.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by misslert on 31.05.2016.
 * Basic class for task items.
 */
public class Task {

    // Data fields for task.
    private long id;
    private String name;
    private String description;
    // Data and Time
    private long expiry;
    private boolean done;
    private boolean favourite;
    // Associations
    private ArrayList<String> contacts;
    private SimpleLocation location;


    // Default constructor
    public Task() {
    }
    // Name constructor
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }
    // ID constructor
    public Task(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public boolean equals(Object other) {
        return other.getClass() == this.getClass() && this.getId() == ((Task) other).getId();
    }

    public String toString() {
        return "Task " + this.id + ": " + this.name + " - " + this.description + ", " + this.expiry;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public ArrayList<String> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<String> contacts) {
        this.contacts = contacts;
    }

    public SimpleLocation getLocation() {
        return location;
    }

    public void setLocation(SimpleLocation location) {
        this.location = location;
    }

}
