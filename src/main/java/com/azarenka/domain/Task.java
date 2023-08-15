package com.azarenka.domain;

/**
 * Represents of .. .
 * <p>
 * Copyright (C) 2023 antazarenko@gmail.com
 * <p>
 * Date: 07/24/2023
 *
 * @author Anton Azarenka
 */
public class Task {

    private String status;
    private String taskName;
    private String owner;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
