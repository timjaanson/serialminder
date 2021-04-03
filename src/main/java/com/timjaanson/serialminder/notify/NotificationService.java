package com.timjaanson.serialminder.notify;

import com.timjaanson.serialminder.email.EmailService;
import com.timjaanson.serialminder.series.dto.Series;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private EmailService emailService;

    @Value("${notify.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${notify.email.to}")
    private String emailTo;

    public void notify(List<Series> seriesList) {
        log.info("notifying regarding new seasons");
        StringBuilder messageText = new StringBuilder("New seasons for:\n");
        seriesList.forEach(series -> {
            String line = series.getName() + " latest season: "+series.getAvailableSeasons() + "\n";
            messageText.append(line);
        });
        if (emailEnabled) {
            emailService.sendSimpleMessage(emailTo, "New seasons notification", messageText.toString());
        }
    }
}
