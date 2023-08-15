package com.azarenka.jirareport.integration;

import org.springframework.stereotype.Component;

/**
 * Represents of .. .
 * <p>
 * Copyright (C) 2023 antazarenko@gmail.com
 * <p>
 * Date: 08/14/2023
 *
 * @author Anton Azarenka
 */
@Component
public class JiraQueryBuilder {

    private String project;
    private String type;
    private String statusStoryInSprint;
    private final static String SEPARATOR = " and ";

    public JiraQueryBuilder addTypeInActiveSprint() {
        statusStoryInSprint = "sprint IN openSprints()";
        return this;
    }

    public JiraQueryBuilder addType(String str) {
        this.type = String.format("type = %s", str);
        return this;
    }

    public JiraQueryBuilder addProject(String projectName) {
        this.project = String.format("project = %s", projectName);
        return this;
    }

    public String build() {
        //todo reimplement in the future
        return project + SEPARATOR +
            type + SEPARATOR +
            statusStoryInSprint;
    }
}
