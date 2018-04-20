package com.waller.alan.familyorganizer;

/**
 * Created by t00053669 on 3/28/2018.
 */

public class Contact {

    private String displayName;
    private String email;
    private String contactOwner;

    public Contact(){
        displayName = "";
        email = "";
        contactOwner = "";
    }

    public Contact(String displayName, String email, String contactOwner) {
        this.displayName = displayName;
        this.email = email;
        this.contactOwner = contactOwner;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String userName) {
        this.email = userName;
    }

    public String getContactOwner() {
        return contactOwner;
    }

    public void setContactOwner(String contactOwner) {
        this.contactOwner = contactOwner;
    }
}
