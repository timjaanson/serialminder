package com.timjaanson.serialminder.service;

import com.timjaanson.serialminder.model.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void notify(List<Series> seriesList) {
        log.info("notifying regarding new seasons");
    }
}
