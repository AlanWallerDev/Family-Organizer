package com.waller.alan.familyorganizer;

/**
 * Created by t00053669 on 3/21/2018.
 */

public class Message {

    private String text;
    private String name;
    private String photoUrl;
    private String receiver;


    public Message() {
    }
    //TODO: Remember you will need to add a who to send to value (reciever?) when the ability to add users is implemented
    public Message(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.receiver = receiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


}
