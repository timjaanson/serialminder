package com.timjaanson.serialminder.traktapi.traktResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TraktSeason {
    private int number;
    private List<TraktEpisode> episodes;
}
