package com.waller.alan.familyorganizer;

/**
 * Created by t00053669 on 3/28/2018.
 */

public class Contact {

    private String displayName;
    private String userName;
    private String contactOwner;
    private String relationship;

    public Contact(String displayName, String userName, String relationship, String contactOwner) {
        this.displayName = displayName;
        this.userName = userName;
        this.relationship = relationship;
        this.contactOwner = contactOwner;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getContactOwner() {
        return contactOwner;
    }

    public void setContactOwner(String contactOwner) {
        this.contactOwner = contactOwner;
    }
}
