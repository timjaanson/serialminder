package com.timjaanson.serialminder.resource;

import com.timjaanson.serialminder.api.TraktClient;
import com.timjaanson.serialminder.api.traktResponseModel.TraktEpisode;
import com.timjaanson.serialminder.api.traktResponseModel.TraktSeason;
import com.timjaanson.serialminder.model.Series;
import com.timjaanson.serialminder.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/series")
@RestController
public class SeriesResource {

    @Autowired
    SeriesService seriesService;

    @Autowired
    TraktClient traktClient;

    //TODO separate trakt client, series and checker endpoints to separate classes with their own paths

    @GetMapping("/{id}/seasons")
    public List<TraktSeason> getSeasonsForShow(@PathVariable String id) {
        return traktClient.getSeasonsById(id);
    }

    @GetMapping("/{id}/seasons/{seasonNr}")
    public List<TraktEpisode> getSeasonsForShow(@PathVariable String id, @PathVariable Integer seasonNr) {
        return traktClient.getEpisodesByIdAndSeason(id, seasonNr);
    }

    @GetMapping("/")
    public List<Series> getAllSeries() {
        return seriesService.getSeries();
    }

    @PostMapping("/add")
    public void addSeries(@RequestBody Series series) {
        seriesService.insertSeries(series);
    }
}
