package com.timjaanson.serialminder.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SeriesRowMapper implements RowMapper<Series> {

    @Override
    public Series mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Series(
                resultSet.getString("id"),
                resultSet.getString("name"),
                resultSet.getInt("available_seasons"),
                (Timestamp) resultSet.getObject("last_checked"),
                resultSet.getBoolean("allowed_for_check"));
    }
}
