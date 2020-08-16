package com.futech.coffeeshop.obj.register;

import java.io.Serializable;

public class RegisterData implements Serializable {

    public static final String ID_KEY = "id";
    public static final String FIRST_NAME_KEY = "firstname";
    public static final String LAST_NAME_KEY = "lastname";
    public static final String PHONE_NUMBER_KEY = "phoneNumber";
    public static final String EMAIL_KEY = "email";
    public static final String LANGUAGE_KEY = "language";
    public static final String ADMIN_KEY = "admin";

    private int id;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String emailAddress;
    private String language;
    private int admin ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        if (firstname == null) return "";
        return firstname;
    }

    public void setFirstname(String firstname) {
        if (firstname == null) return;
        if (firstname.length() < 51) this.firstname = firstname;
    }

    public String getLastname() {
        if (lastname == null) return "";
        return lastname;
    }

    public void setLastname(String lastname) {
        if (lastname == null) return;
        if (lastname.length() < 51) this.lastname = lastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() < 14) this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        if (emailAddress == null) return;
        this.emailAddress = emailAddress;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isAdmin() {
        return admin == 1;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin ? 1 : 0;
    }

    public String getFullName() {
        if (getFirstname().equals("") && getLastname().equals("")) return "";
        return getFirstname() + " " + getLastname();
    }
}
