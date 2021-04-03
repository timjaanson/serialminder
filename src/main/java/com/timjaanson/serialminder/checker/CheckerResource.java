package com.timjaanson.serialminder.checker;

import com.timjaanson.serialminder.series.dto.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/check")
@RestController
public class CheckerResource {
    @Autowired
    private CheckerService checkerService;

    @PutMapping
    public List<Series> checkForNewSeasonsForAllShows() {
        return checkerService.checkAndNotifyForNewSeasonsForAllShows();
    }
}
