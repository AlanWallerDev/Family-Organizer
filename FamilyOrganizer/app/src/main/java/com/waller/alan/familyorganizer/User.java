package com.waller.alan.familyorganizer;

/**
 * Created by t00053669 on 3/28/2018.
 */

public class User {
    //TODO: figure out how to create user database that doesnt cause duplicates
    private String username;
    private String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
