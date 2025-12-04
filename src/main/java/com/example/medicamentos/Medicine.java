package com.example.medicamentos;


import java.io.Serializable;
import java.util.Date;


public class Medicine implements Serializable {
    private String id;
    private String name;
    private String description;
    private long timeMillis; // horário em millis
    private boolean taken;


    public Medicine() {}


    public Medicine(String id, String name, String description, long timeMillis, boolean taken) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.timeMillis = timeMillis;
        this.taken = taken;
    }


    // getters e setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public long getTimeMillis() { return timeMillis; }
    public void setTimeMillis(long timeMillis) { this.timeMillis = timeMillis; }
    public boolean isTaken() { return taken; }
    public void setTaken(boolean taken) { this.taken = taken; }
}

