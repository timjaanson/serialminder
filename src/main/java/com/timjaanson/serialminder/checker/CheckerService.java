package com.timjaanson.serialminder.checker;

import com.timjaanson.serialminder.notify.NotificationService;
import com.timjaanson.serialminder.series.SeriesService;
import com.timjaanson.serialminder.series.dto.Series;
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
public class CheckerService implements InitializingBean {
    private final Logger log = LoggerFactory.getLogger(CheckerService.class);

    @Value("${checker.cron.enabled:false}")
    private boolean cronEnabled;

    @Value("${checker.startup.enabled:false}")
    private boolean runOnStartup;

    @Autowired
    private SeriesService seriesService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (runOnStartup) {
            checkAndNotifyForNewSeasonsForAllShows();
        } else {
            log.info("CheckerService disabled on startup");
        }

        if (!cronEnabled) {
            log.info("Scheduled CheckerService is disabled");
        }
    }

    // at 15:00 every day
    @Scheduled(cron = "${checker.cron.value:0 0 15 * * *}")
    public void scheduledCheck() {
        if (cronEnabled) {
            checkAndNotifyForNewSeasonsForAllShows();
        }
    }

    public List<Series> checkAndNotifyForNewSeasonsForAllShows() {
        List<Series> seriesWithNewSeasons = checkForNewSeasonsForAllShows();

        if (!seriesWithNewSeasons.isEmpty()) {
            notificationService.notify(seriesWithNewSeasons);
        }
        return seriesWithNewSeasons;
    }

    List<Series> checkForNewSeasonsForAllShows() {
        log.info("Starting new season check for all shows");
        List<Series> seriesForChecking = seriesService.getSeriesForNewSeasonCheck();
        List<Series> seriesWithNewSeasons = new ArrayList<>();
        seriesForChecking.forEach(series -> {
            log.info("Checking series {}...", series.getName());
            if (seriesService.isNewSeasonAvailable(series)) {
                Series updatedSeries = seriesService.getSeriesById(series.getId());
                log.info("New season for series: {}({}), currently available seasons: {}", updatedSeries.getName(), updatedSeries.getId(), updatedSeries.getAvailableSeasons());
                seriesWithNewSeasons.add(updatedSeries);
            } else {
                log.info("No new season for series: {}({}), currently available seasons: {}", series.getName(), series.getId(), series.getAvailableSeasons());
            }
        });

        return seriesWithNewSeasons;
    }

}
