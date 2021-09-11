package com.timjaanson.serialminder.series.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Series {
    private String id;
    private String name;
    private Integer availableSeasons;
    private Timestamp lastChecked;
    private boolean allowedForCheck;
}
