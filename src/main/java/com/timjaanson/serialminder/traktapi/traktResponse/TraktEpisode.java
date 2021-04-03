package com.timjaanson.serialminder.traktapi.traktResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TraktEpisode {
    private int season;
    private int number;
    private String title;
}
