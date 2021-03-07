package com.timjaanson.serialminder.service;

import com.timjaanson.serialminder.model.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@EnableScheduling
public class ReminderService implements InitializingBean {
    private final Logger log = LoggerFactory.getLogger(ReminderService.class);

    @Value("${reminder.cron.enabled:false}")
    private boolean cronEnabled;

    @Value("${reminder.startup.enabled:false}")
    private boolean runOnStartup;

    @Autowired
    private SeriesService seriesService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (runOnStartup) {
            checkForNewSeasonsForAllShows();
        } else {
            log.info("ReminderService disabled on startup");
        }

        if (!cronEnabled) {
            log.info("Scheduled ReminderService is disabled");
        }
    }

    @Scheduled(cron = "${reminder.cron.value:0 0 15 * * FRI}")
    public void scheduledCheck() {
        if (cronEnabled) {
            checkForNewSeasonsForAllShows();
        }
    }

    public void checkForNewSeasonsForAllShows() {
        log.info("Starting new season check for all shows");
        List<Series> seriesForChecking = seriesService.getSeriesForNewSeasonCheck();
        List<Series> seriesWithNewSeasons = new ArrayList<>();
        seriesForChecking.forEach(series -> {
            log.info("Checking series {}...", series.getName());
            if (seriesService.isNewSeasonAvailable(series)) {
                //TODO return new series object with updated seasons info
                log.info("New season for series: {}({}), currently available seasons: {}", series.getName(), series.getId(), series.getAvailableSeasons() + 1);
                seriesWithNewSeasons.add(series);
            } else {
                log.info("No new season for series: {}({}), currently available seasons: {}", series.getName(), series.getId(), series.getAvailableSeasons());
            }
        });

        if (!seriesWithNewSeasons.isEmpty()) {
            notificationService.notify(seriesWithNewSeasons);
        }
    }


}
