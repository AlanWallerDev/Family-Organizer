package com.waller.alan.familyorganizer;

/**
 * Created by Alan on 4/19/2018.
 */

public class Event {
    private String owner;
    private String name;
    private long startDate;
   // private long endDate;
    private String description;

    public Event() {
        owner = "";
        startDate = 0;
        name = "";
        //endDate = 0;
        description = "";
    }

    public Event(String owner,String name, long startDate, String description) {
        this.owner = owner;
        this.startDate = startDate;
        this.description = description;
        //this.endDate = endDate;
        this.name = name;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }
}
