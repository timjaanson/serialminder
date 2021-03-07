package com.timjaanson.serialminder.service;

import com.timjaanson.serialminder.api.TraktClient;
import com.timjaanson.serialminder.errors.exceptions.SeriesAlreadyExistsException;
import com.timjaanson.serialminder.model.Series;
import com.timjaanson.serialminder.repository.SeriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeriesService {

    private final Logger log = LoggerFactory.getLogger(SeriesService.class);

    @Autowired
    SeriesRepository seriesRepository;

    @Autowired
    TraktClient traktClient;

    public List<Series> getSeries() {
        return seriesRepository.getAllSeries();
    }

    public List<Series> getSeriesForNewSeasonCheck() {
        return seriesRepository.getAllSeriesThatAreAllowedForNewSeasonCheck();
    }

    public void insertSeries(Series series) {
        if (!seriesRepository.addSeries(series)) {
            throw new SeriesAlreadyExistsException("Series with id "+series.getId()+" already exists");
        }
    }

    public boolean isNewSeasonAvailable(Series series) {
        Integer seasonsCurrently = seriesRepository.getSeasonsCountForSeriesById(series.getId());
        Integer newSeason = seasonsCurrently + 1;
        if (traktClient.getEpisodesByIdAndSeason(series.getId(), newSeason).isEmpty()) {
            return false;
        } else {
            seriesRepository.updateSeasonsCountForSeriesById(series.getId(), newSeason);
            return true;
        }
    }
}
