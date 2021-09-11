package com.timjaanson.serialminder.series;

import com.timjaanson.serialminder.series.dto.Series;
import com.timjaanson.serialminder.series.exceptions.SeriesAlreadyExistsException;
import com.timjaanson.serialminder.traktapi.TraktClient;
import com.timjaanson.serialminder.traktapi.traktResponse.TraktEpisode;
import com.timjaanson.serialminder.traktapi.traktResponse.TraktSeason;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeriesServiceTest {

    @Mock
    TraktClient traktClient;

    @Mock
    SeriesRepository seriesRepository;

    @Captor
    ArgumentCaptor<Series> seriesCaptor;

    @InjectMocks
    SeriesService seriesService;

    @Test
    public void getLatestAvailableSeason() {
        TraktEpisode ep1 = new TraktEpisode(1,1, "title");
        List<TraktEpisode> episodes = new ArrayList<>(List.of(ep1));
        TraktSeason season1 = new TraktSeason(1, episodes);
        List<TraktSeason> seasons = new ArrayList<>(List.of(season1));

        when(traktClient.getSeasonsAndEpisodesById(anyString())).thenReturn(seasons);

        assertEquals(1, seriesService.getLatestAvailableSeason("id"));
    }

    @Test
    public void getLatestAvailableSeasonWithEmptySeason() {
        TraktEpisode ep1 = new TraktEpisode(1,1, "title");
        List<TraktEpisode> episodes = new ArrayList<>(List.of(ep1));
        TraktSeason season1 = new TraktSeason(1, episodes);
        List<TraktSeason> seasons = new ArrayList<>(Arrays.asList(
                season1,
                new TraktSeason(2, new ArrayList<>())));

        when(traktClient.getSeasonsAndEpisodesById(anyString())).thenReturn(seasons);

        assertEquals(1, seriesService.getLatestAvailableSeason("id"));
    }

    @Test
    public void insertSeriesWithoutSeasonsCount() {
        Series series = new Series();
        series.setId("id");
        series.setName("name");

        seriesService.insertSeries(series);

        verify(seriesRepository, times(1)).addSeries(any());
    }

    @Test
    public void insertSeriesWithSeasonsCount() {
        Series series = new Series();
        series.setId("id");
        series.setName("name");
        series.setAvailableSeasons(5);

        seriesService.insertSeries(series);

        verify(seriesRepository).addSeries(seriesCaptor.capture());
        Series s = seriesCaptor.getValue();
        assertEquals(5, s.getAvailableSeasons());
        verify(seriesRepository, times(1)).addSeries(any());
    }

    @Test
    public void insertSeriesDuplicate() {
        Series series = new Series();
        series.setId("id");
        series.setName("name");

        when(seriesRepository.getSeriesById(series.getId())).thenReturn(series);

        assertThrows(SeriesAlreadyExistsException.class, () -> seriesService.insertSeries(series));
        verify(seriesRepository, never()).addSeries(any());
    }

    @Test
    public void insertSeriesWithoutId() {
        Series series = new Series();

        assertThrows(IllegalArgumentException.class, () -> seriesService.insertSeries(series));
        verify(seriesRepository, never()).addSeries(any());
    }

    @Test
    public void insertSeriesWithoutName() {
        Series series = new Series();
        series.setId("id");

        assertThrows(IllegalArgumentException.class, () -> seriesService.insertSeries(series));
        verify(seriesRepository, never()).addSeries(any());
    }

    @Test
    public void isNewSeasonAvailable() {
        TraktEpisode ep1 = new TraktEpisode(1,1, "title");
        List<TraktEpisode> s1episodes = new ArrayList<>(List.of(ep1));
        TraktSeason season1 = new TraktSeason(1, s1episodes);

        TraktEpisode ep2 = new TraktEpisode(2,1, "title");
        List<TraktEpisode> s2episodes = new ArrayList<>(List.of(ep2));
        TraktSeason season2 = new TraktSeason(2, s2episodes);

        List<TraktSeason> seasons = new ArrayList<>(Arrays.asList(season1, season2));

        Series series = new Series();
        series.setId("id");


        when(traktClient.getSeasonsAndEpisodesById(series.getId())).thenReturn(seasons);
        when(seriesRepository.getSeasonsCountForSeriesById(series.getId())).thenReturn(1);


        assertTrue(seriesService.isNewSeasonAvailable(series));
    }

    @Test
    public void newSeasonNotAvailable() {
        TraktEpisode ep1 = new TraktEpisode(1,1, "title");
        List<TraktEpisode> s1episodes = new ArrayList<>(List.of(ep1));
        TraktSeason season1 = new TraktSeason(1, s1episodes);

        List<TraktSeason> seasons = new ArrayList<>(List.of(season1));

        Series series = new Series();
        series.setId("id");


        when(traktClient.getSeasonsAndEpisodesById(series.getId())).thenReturn(seasons);
        when(seriesRepository.getSeasonsCountForSeriesById(series.getId())).thenReturn(1);


        assertFalse(seriesService.isNewSeasonAvailable(series));
    }
}