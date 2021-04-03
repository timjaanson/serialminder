package com.timjaanson.serialminder.traktapi;

import com.timjaanson.serialminder.traktapi.traktResponse.TraktEpisode;
import com.timjaanson.serialminder.traktapi.traktResponse.TraktSeason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/trakt")
@RestController
public class TraktResource {

    @Autowired
    private TraktClient traktClient;

    @GetMapping("/{id}/seasons")
    public List<TraktSeason> getSeasonsForShow(@PathVariable String id) {
        return traktClient.getSeasonsById(id);
    }

    @GetMapping("/{id}/seasons/{seasonNr}")
    public List<TraktEpisode> getEpisodesByIdAndSeason(@PathVariable String id, @PathVariable Integer seasonNr) {
        return traktClient.getEpisodesByIdAndSeason(id, seasonNr);
    }
}
