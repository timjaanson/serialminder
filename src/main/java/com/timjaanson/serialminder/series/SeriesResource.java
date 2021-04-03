package com.timjaanson.serialminder.series;

import com.timjaanson.serialminder.series.dto.Series;
import com.timjaanson.serialminder.series.exceptions.SeriesAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/series")
@RestController
public class SeriesResource {

    @Autowired
    SeriesService seriesService;

    @GetMapping("/")
    public List<Series> getAllSeries() {
        return seriesService.getSeries();
    }

    @PostMapping("/add")
    public void addSeries(@RequestBody Series series) {
        seriesService.insertSeries(series);
    }

    @PostMapping("/addMultiple")
    public void addMultipleSeries(@RequestBody List<Series> seriesList) {
        seriesList.forEach(series -> {
            try {
                seriesService.insertSeries(series);
            } catch (SeriesAlreadyExistsException e) {/* suppress */}
        });
    }
}
