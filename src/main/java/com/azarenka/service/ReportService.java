package com.azarenka.service;

import com.azarenka.jirareport.integration.JiraIntegrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
public class ReportService {
    @Autowired
    private JiraIntegrationService jiraIntegrationService;
    @Autowired
    private ReportPrinter reportPrinter;

    @Value("${outlook.path}")
    private String outlookPath;
    @Value("${mail.to}")
    private String mailTo;
    @Value("${mail.cc}")
    private String mailCc;

    /**
     * Generates report and opens it in Outlook app.
     */
    public void openReportInOutlook() {
        try {
            Path reportPath = Files.createTempFile("report", "report.html");
            reportPrinter.print(jiraIntegrationService.getStories(), reportPath);
            new ProcessBuilder(outlookPath, "/c", "ipm.note",
                "/a", reportPath.toAbsolutePath().toString(),
                "/m", String.format("%s?cc=%s&subject=%s", mailTo, mailCc,
                String.format("Daily Status (%s)", DateTimeFormatter.ofPattern("MM/dd/yyyy").format(LocalDate.now()))))
                .start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
