package com.timjaanson.serialminder.series;

import com.timjaanson.serialminder.series.dto.Series;
import com.timjaanson.serialminder.series.exceptions.SeriesAlreadyExistsException;
import com.timjaanson.serialminder.traktapi.TraktClient;
import com.timjaanson.serialminder.traktapi.traktResponse.TraktSeason;
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

    public Series getSeriesById(String id) {
        return seriesRepository.getSeriesById(id);
    }

    public List<Series> getSeriesForNewSeasonCheck() {
        return seriesRepository.getAllSeriesThatAreAllowedForNewSeasonCheck();
    }

    public void insertSeries(Series series) {
        log.info("Adding new series: {}", series);
        String id = series.getId();
        if (id == null) {
            throw new IllegalArgumentException("Missing id for new series");
        }
        if (getSeriesById(id) == null) {
            Series seriesToBeAdded = new Series();
            seriesToBeAdded.setId(id);

            if (series.getName() == null) {
                //TODO if name not set, get it by id from trakt
                throw new IllegalArgumentException("Missing name for new series");
            } else {
                seriesToBeAdded.setName(series.getName());
            }

            if (series.getAvailableSeasons() == null) {
                seriesToBeAdded.setAvailableSeasons(getLatestAvailableSeason(id));
            } else {
                seriesToBeAdded.setAvailableSeasons(series.getAvailableSeasons());
            }

            seriesRepository.addSeries(seriesToBeAdded);
        } else {
            log.warn("Series with id {} already exists", id);
            throw new SeriesAlreadyExistsException("Series with id "+id+" already exists");
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

    Integer getLatestAvailableSeason(String id) {
        //TODO investigate trakt api next episode/last episode

        // some shows have season information set without any actual episodes out/confirmed
        List<TraktSeason> allSeasons = traktClient.getSeasonsAndEpisodesById(id);
        int lastActuallyAvailableSeason = 0;
        for (int i = allSeasons.size() - 1; i >= 0; i--) {
            TraktSeason season = allSeasons.get(i);
            if (!season.getEpisodes().isEmpty()) {
                lastActuallyAvailableSeason = season.getNumber();
                break;
            }
        }
        return lastActuallyAvailableSeason;
    }
}
