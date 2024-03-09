package org.javafxtest.service;

import javafx.scene.control.Button;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nikolay Boyko
 */

@Slf4j
@Service
public class ScheduledService {

    private final NewsService newsService;
    private static final long schedulerNewsUpdateTime = 1200000L;
    private final List<Object> objectsToNotify = new ArrayList<>();

    public ScheduledService(NewsService newsService) {
        this.newsService = newsService;
    }

    //    @Scheduled(cron = "*/20 * * * *")
    @Scheduled(fixedRate = 25000)
    public void checkUpdatesForNews() {
        if (newsService.checkForUpdates()) {
            notifyObservers();
        } else {
            log.info("No new news by now!");
        }
    }

    public void addObjectToNotify(Object object) {
        if (object != null) {
            this.objectsToNotify.add(object);
        }
    }

    public void notifyObservers() {
        for (Object obj : this.objectsToNotify) {
            if (obj instanceof Button) {
                Button btn = (Button) obj;
                btn.setDisable(false);
                btn.setOpacity(100);
            }
        }
    }

}
