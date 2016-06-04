package com.android.master.mad.todo.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by misslert on 31.05.2016.
 * Basic class for task items.
 */
public class Task {

    // Data fields for task.
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    // Data and Time
    @SerializedName("expiry")
    private long expiry;
    @SerializedName("done")
    private boolean done;
    @SerializedName("favourite")
    private boolean favourite;
    // Associations
    @SerializedName("contacts")
    private String simpleContacts;
    private transient ArrayList<String> contacts;
    // @SerializedName("location")
    // private SimpleLocation location;


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

    public String getSimpleContacts() {
        return simpleContacts;
    }

    public void setSimpleContacts(String simpleContacts) {
        this.simpleContacts = simpleContacts;
    }

    public ArrayList<String> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<String> contacts) {
        this.contacts = contacts;
        updateSimpleContacts();
    }

    public void addContact(String contact){
        contacts.add(contact);
        updateSimpleContacts();
    }

    public void removeContact(String contact){
        contacts.remove(contact);
        updateSimpleContacts();
    }

//    public SimpleLocation getLocation() {
//        return location;
//    }
//
//    public void setLocation(SimpleLocation location) {
//        this.location = location;
//    }

    private void updateSimpleContacts(){
        this.simpleContacts = this.contacts.toArray().toString();
    }
}
