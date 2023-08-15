package com.azarenka.service;

import com.azarenka.domain.Story;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

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
public class ReportPrinter {

    private static final String STORY_FORMAT = "<div style='margin : 0.4em;'><b><a href='%s'>%s</a> %s</b></div>";
    private static final String IN_PROGRESS_FORMAT =
        "<li style='margin-top : 0.15em;'>%s - <b><font color='orange'>in progress</font> [%s]</b></li>";
    private static final String COMPLETED_FORMAT =
        "<li style='margin-top : 0.15em;'>%s - <b><font color='green'>completed</font> [%s]</b></li>";

    @Value("${mail.salutation.name}")
    private String salutationName;
    @Value("${mail.signature.name}")
    private String signatureName;

    /**
     * Writes {@link Story}ies to a file in HTML format.
     *
     * @param stories list of {@link Story} to print in file
     * @param path    {@link Path} to file
     * @throws IOException if an I/O error occurs writing to or creating the file
     */
    public void print(Set<Story> stories, Path path) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(String.format("<p>Hi %s,</p><p>Today:</p>", salutationName));
        stories.forEach(story -> {
            stringBuffer.append(String.format(STORY_FORMAT, story.getLink(), story.getKey(), story.getName()));
            stringBuffer.append("<ul>");
            story.getTasks().forEach(task -> stringBuffer.append(String.format(task.getStatus().equals("Done")
                ? COMPLETED_FORMAT : IN_PROGRESS_FORMAT, task.getTaskName(), task.getOwner())));
            stringBuffer.append("</ul>");
        });
        stringBuffer.append("Best regards,");
        Files.write(path, stringBuffer.toString().getBytes());
    }
}
