package com.azarenka.jirareport.integration;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;

import javax.annotation.PostConstruct;

/**
 * Represents of .. .
 * <p>
 * Copyright (C) 2023 antazarenko@gmail.com
 * <p>
 * Date: 07/17/2023
 *
 * @author Anton Azarenka
 */
@Component
public class JiraConnector {

    @Value("${jira.username}")
    private String username;
    @Value("${jira.token}")
    private String token;
    @Value("${jira.endpoint}")
    private String jiraEndPoint;

    private JiraRestClient jiraRestClient;

    public JiraRestClient init() {
        return new AsynchronousJiraRestClientFactory()
            .create(URI.create(jiraEndPoint), new BasicHttpAuthenticationHandler(username, token));
    }

    public JiraRestClient getJiraRestClient() {
        return jiraRestClient;
    }
}
