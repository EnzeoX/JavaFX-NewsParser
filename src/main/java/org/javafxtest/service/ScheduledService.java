package org.javafxtest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@Service
public class ScheduledService {

    private final NewsService newsService;
    private static final long schedulerNewsUpdateTime = 1200000L;

    public ScheduledService(NewsService newsService) {
        this.newsService = newsService;
    }

//    @Scheduled(cron = "*/20 * * * *")
//    @Scheduled(fixedDelay = schedulerNewsUpdateTime)
    public void checkUpdatesForNews() {
        newsService.checkForUpdates();
    }
}
