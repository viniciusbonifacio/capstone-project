package com.dontletbehind.domain.entity;

import java.io.Serializable;

/**
 * Representation of a Task to be done.
 */
public class TaskEntity implements Serializable {

    private int id;
    private String title;
    private String description;
    private long timer;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }
}
