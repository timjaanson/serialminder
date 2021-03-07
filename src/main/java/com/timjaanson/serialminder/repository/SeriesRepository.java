package com.timjaanson.serialminder.repository;

import com.timjaanson.serialminder.model.Series;
import com.timjaanson.serialminder.model.SeriesRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class SeriesRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Series> getAllSeries() {
        String sql = "SELECT * FROM series";
        return jdbcTemplate.query(sql, new SeriesRowMapper());
    }

    public List<Series> getAllSeriesThatAreAllowedForNewSeasonCheck() {
        String sql = "SELECT * FROM series WHERE allowed_for_check = true";
        return jdbcTemplate.query(sql, new SeriesRowMapper());
    }

    public Series getSeries(String id) {
        String sql = "SELECT * FROM series WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new SeriesRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Integer getSeasonsCountForSeriesById(String id) {
        String select = "SELECT available_seasons FROM series WHERE id = ?";
        String updateLastChecked = "UPDATE series SET last_checked = ? WHERE id = ?";
        try {
            Integer seasonCount = jdbcTemplate.queryForObject(select, Integer.class, id);
            jdbcTemplate.update(updateLastChecked, new Timestamp(System.currentTimeMillis()), id);
            return seasonCount;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updateSeasonsCountForSeriesById(String id, Integer seasonsCount) {
        String sql = "UPDATE series SET available_seasons = ? WHERE id = ?";
        jdbcTemplate.update(sql, seasonsCount, id);
    }

    public boolean addSeries(Series series) {
        if (getSeries(series.getId()) == null) {
            String sql = "INSERT INTO series (id, name, available_seasons, last_checked) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    series.getId(),
                    series.getName(),
                    series.getAvailableSeasons(),
                    series.getLastChecked());
            return true;
        } else {
            return false;
        }
    }

}
