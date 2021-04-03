package com.timjaanson.serialminder.series;

import com.timjaanson.serialminder.series.dto.Series;
import com.timjaanson.serialminder.series.exceptions.SeriesAlreadyExistsException;
import com.timjaanson.serialminder.traktapi.TraktClient;
import com.timjaanson.serialminder.traktapi.traktResponse.TraktSeason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    public Series getSeriesById(String id) {
        return seriesRepository.getSeriesById(id);
    }

    public List<Series> getSeriesForNewSeasonCheck() {
        return seriesRepository.getAllSeriesThatAreAllowedForNewSeasonCheck();
    }

    public void insertSeries(Series series) {
        if (getSeriesById(series.getId()) == null) {
            if (series.getAvailableSeasons() == null) {
                series.setAvailableSeasons(getLatestAvailableSeason(series.getId()));
            }
            //TODO if name not set, get it by id
            seriesRepository.addSeries(series);
        } else {
            log.warn("Series with id {} already exists", series.getId());
            throw new SeriesAlreadyExistsException("Series with id "+series.getId()+" already exists");
        }
    }

    public boolean isNewSeasonAvailable(Series series) {
        Integer seasonsCurrently = seriesRepository.getSeasonsCountForSeriesById(series.getId());
        int latestAvailableSeason = getLatestAvailableSeason(series.getId());
        if (latestAvailableSeason > seasonsCurrently) {
            seriesRepository.updateSeasonsCountForSeriesById(series.getId(), latestAvailableSeason);
            return true;
        }
        return false;
    }

    private Integer getLatestAvailableSeason(String id) {
        //TODO investigate trakt api next episode/last episode

        // some shows have season information set without any actual episodes out/confirmed
        List<TraktSeason> allSeasons = traktClient.getSeasonsAndEpisodesById(id);
        Collections.reverse(allSeasons);
        int lastActuallyAvailableSeason = 0;
        for (TraktSeason season : allSeasons){
            if (!season.getEpisodes().isEmpty()) {
                lastActuallyAvailableSeason = season.getNumber();
                break;
            }
        }
        return lastActuallyAvailableSeason;
    }
}
