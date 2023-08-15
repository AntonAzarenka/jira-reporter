package com.azarenka;

import com.azarenka.service.ReportService;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Represents of .. .
 * <p>
 * Copyright (C) 2023 antazarenko@gmail.com
 * <p>
 * Date: 07/17/2023
 *
 * @author Anton Azarenka
 */
public class Main {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("com/azarenka/jirareporter/spring-context.xml");
        ReportService reportService = context.getBean(ReportService.class);
        reportService.openReportInOutlook();
    }
}