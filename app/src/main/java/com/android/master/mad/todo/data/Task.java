package com.android.master.mad.todo.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by MISSLERT on 31.05.2016.
 * Basic class for task items.
 */
public class Task implements Parcelable {

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
    private ArrayList<String> contacts;

    // Default constructor
    public Task() {
        this.id = -1;
    }
    // Name constructor
    public Task(String name, String description) {
        this.id = -1;
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
        return "Task " + this.id + ": " + this.name + " - " + this.description
                + "(" + this.done + "/" + this.favourite + ")"
                +  ", " + this.expiry
                + " with associated contacts: " + this.contacts;
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

    public String getContactsString() {
        if(contacts != null){
            return this.contacts.toString().substring(1, this.contacts.toString().length()-1 );
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void setContacts(ArrayList<String> contacts) {
        this.contacts = contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = new ArrayList<>();
        if(contacts != null) {
            String[] converter = contacts.split(", ");
            Collections.addAll(this.contacts, converter);
        }
    }

    public void addContact(String contact){
        if(!contacts.contains(contact)) contacts.add(contact);
        Log.v("TASK: ", "contacts is: " + contacts);
        Log.v("TASK: ", "contacts size: " + contacts.size());
    }

    public void removeContact(String contact){
        contacts.remove(contact);
        Log.v("TASK: ", "contacts size: " + contacts.size());
        Log.v("TASK: ", "contacts is: " + contacts);
    }

    public boolean hasContacts(){
        return contacts != null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeLong(this.expiry);
        dest.writeString(this.description);
        dest.writeByte(this.done ? (byte) 1 : (byte) 0);
        dest.writeByte(this.favourite ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.contacts);
    }

    protected Task(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.expiry = in.readLong();
        this.description = in.readString();
        this.done = in.readByte() != 0;
        this.favourite = in.readByte() != 0;
        this.contacts = in.createStringArrayList();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
