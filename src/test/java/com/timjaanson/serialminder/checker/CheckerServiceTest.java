package com.timjaanson.serialminder.checker;

import com.timjaanson.serialminder.series.SeriesService;
import com.timjaanson.serialminder.series.dto.Series;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckerServiceTest {

    @Mock
    SeriesService seriesService;

    @InjectMocks
    CheckerService checkerService;

    @Test
    public void checkForNewSeasonsForAllShows() {
        List<Series> seriesList = new ArrayList<>(Arrays.asList(
            Series.builder().id("id1").name("name").availableSeasons(1).build(),
            Series.builder().id("id2").name("name").availableSeasons(1).build()
        ));

        when(seriesService.getSeriesForNewSeasonCheck()).thenReturn(seriesList);
        when(seriesService.isNewSeasonAvailable(any())).thenReturn(true);
        when(seriesService.getSeriesById(seriesList.get(0).getId())).thenReturn(seriesList.get(0));
        when(seriesService.getSeriesById(seriesList.get(1).getId())).thenReturn(seriesList.get(1));

        List<Series> seriesWithNewSeasons = checkerService.checkForNewSeasonsForAllShows();
        assertEquals(2, seriesWithNewSeasons.size());
        assertEquals("id1", seriesWithNewSeasons.get(0).getId());
        assertEquals("id2", seriesWithNewSeasons.get(1).getId());
    }

    @Test
    public void checkForNewSeasonsNotAllHaveUpdates() {
        List<Series> seriesList = new ArrayList<>(Arrays.asList(
                Series.builder().id("id1").name("name").availableSeasons(1).build(),
                Series.builder().id("id2").name("name").availableSeasons(1).build()
        ));

        when(seriesService.getSeriesForNewSeasonCheck()).thenReturn(seriesList);
        when(seriesService.isNewSeasonAvailable(seriesList.get(0))).thenReturn(true);
        when(seriesService.isNewSeasonAvailable(seriesList.get(1))).thenReturn(false);
        when(seriesService.getSeriesById(seriesList.get(0).getId())).thenReturn(seriesList.get(0));

        List<Series> seriesWithNewSeasons = checkerService.checkForNewSeasonsForAllShows();
        assertEquals(1, seriesWithNewSeasons.size());
        assertEquals("id1", seriesWithNewSeasons.get(0).getId());
    }
}