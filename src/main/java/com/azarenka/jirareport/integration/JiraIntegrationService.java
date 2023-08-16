package com.azarenka.jirareport.integration;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.atlassian.jira.rest.client.api.domain.User;
import com.azarenka.domain.Story;
import com.azarenka.domain.Task;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

/**
 * Represents of .. .
 * <p>
 * Copyright (C) 2023 antazarenko@gmail.com
 * <p>
 * Date: 07/24/2023
 *
 * @author Anton Azarenka
 */
@Component
public class JiraIntegrationService {

    private static Logger LOGGER = LoggerFactory.getLogger(JiraIntegrationService.class);
    @Value("${story.link}")
    private String storyLink;
    @Value("${team.name}")
    private String teamName;
    @Value("${team.members}")
    private String[] memberNames;
    @Value("#{'${jira.issue.completed_statuses}'.split(',')}")
    private List<String> issueCompletedStatuses;
    @Value("#{'${jira.issue.progress_statuses}'.split(',')}")
    private List<String> issueProgressStatuses;
    @Value("${jira.project.name}")
    private String projectName;
    private IssueRestClient issueClient;
    private JiraRestClient jiraRestClient;

    @Autowired
    private JiraConnector jiraConnector;
    @Autowired
    private JiraQueryBuilder builder;

    @PostConstruct
    public void init() {
        LOGGER.info("Init connection...");
        jiraRestClient = jiraConnector.init();
        issueClient = jiraRestClient.getIssueClient();
        LOGGER.info("Connection established.");
    }

    public Set<Story> getStories() {
        Set<Story> stories = new HashSet<>();
        SearchResult searchResult;
        LOGGER.info("Start to search stories...");
        String query = builder.addProject(projectName).addType("Story").addTypeInActiveSprint().build();
        try {
            searchResult =
                jiraRestClient.getSearchClient()
                    .searchJql(query)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        StreamSupport.stream(searchResult.getIssues().spliterator(), false)
            .forEach(issue -> {
                Story story = createStory(issue);
                if (story.getTasks().size() > 0) {
                    stories.add(story);
                }
            });

        LOGGER.info("Count stories found={}", stories.size());
        return stories;
    }

    private Set<Task> getSubtasks(Issue issue) {
        Set<Task> tasks = new HashSet<>();
        Iterable<Subtask> subtasks = issue.getSubtasks();
        StreamSupport.stream(subtasks.spliterator(), false)
            .filter(subtask -> issueCompletedStatuses.contains(subtask.getStatus().getName()))
            .forEach(st -> {
                try {
                    Issue is = issueClient.getIssue(st.getIssueKey()).get();
                    LocalDateTime updateDate = LocalDateTime.ofInstant(is.getUpdateDate().toDate().toInstant(),
                        ZoneId.systemDefault()).minusHours(8);
                    int dayOffset = DayOfWeek.MONDAY != LocalDate.now().getDayOfWeek() ? 1 : 3;
                    LocalDateTime startDate =
                        LocalDateTime.of(LocalDate.now().minusDays(dayOffset), LocalTime.of(8, 0));
                    LocalDateTime finishDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0));
                    if (updateDate.isAfter(startDate) && updateDate.isBefore(finishDate)) {
                        tasks.add(createTask(st));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }

            });
        StreamSupport.stream(subtasks.spliterator(), false)
            .filter(subtask -> issueProgressStatuses.contains(subtask.getStatus().getName()))
            .forEach(st -> {
                try {
                    tasks.add(createTask(st));
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        return tasks;
    }

    private Task createTask(Subtask subtask) throws ExecutionException, InterruptedException {
        Task task = new Task();
        task.setTaskName(subtask.getSummary());
        task.setStatus(subtask.getStatus().getName());
        Issue issue = issueClient.getIssue(subtask.getIssueKey()).get();
        User assignee = issue.getAssignee();
        task.setOwner(Objects.nonNull(assignee) &&
            Objects.nonNull(assignee.getDisplayName())
                ? assignee.getDisplayName()
                : StringUtils.EMPTY);
        return task;
    }

    private Story createStory(Issue issue) {
        Story story = new Story();
        story.setId(issue.getId().toString());
        story.setKey(issue.getKey());
        story.setName(issue.getSummary());
        story.setLink(String.format(storyLink, issue.getKey()));
        story.setTasks(getSubtasks(issue));
        return story;
    }
}


