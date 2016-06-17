package com.android.master.mad.todo.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MISSLERT on 05.06.2016.
 * Basic class for user data.
 */
public class User {

    @SerializedName("email")
    private String mail;
    @SerializedName("pwd")
    private String pwd;

    @SuppressWarnings("unused")
    // Default constructor
    public User(){

    }

    public User(String mail, String pwd) {
        this.mail = mail;
        this.pwd = pwd;
    }

    @SuppressWarnings("unused")
    public String getPwd() {
        return pwd;
    }

    @SuppressWarnings("unused")
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @SuppressWarnings("unused")
    public String getMail() {
        return mail;
    }

    @SuppressWarnings("unused")
    public void setMail(String mail) {
        this.mail = mail;
    }
}
