package com.timjaanson.serialminder.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timjaanson.serialminder.api.traktResponseModel.TraktEpisode;
import com.timjaanson.serialminder.api.traktResponseModel.TraktSeason;
import com.timjaanson.serialminder.errors.exceptions.TraktAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class TraktClient {
    private final Logger log = LoggerFactory.getLogger(TraktClient.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${trakt.api.key}")
    private String apiKey;

    @Value("${trakt.api.version:2}")
    private String apiVersion;

    @Value("${trakt.api.url:https://api.trakt.tv}")
    private String apiBaseUrl;

    public List<TraktSeason> getSeasonsById(String id) {
        HTTPResponseObject res = getFromTrakt("/shows/"+id+"/seasons");
        try {
            return objectMapper.readValue(res.getResponseBody(), new TypeReference<>() {});
        } catch (Throwable t) {
            log.error("Error parsing response for show: {}", id, t);
            throw new RuntimeException(t);
        }
    }

    public List<TraktEpisode> getEpisodesByIdAndSeason(String id, Integer season) {
        HTTPResponseObject res = getFromTrakt("/shows/"+id+"/seasons/"+season);
        if (res.getStatus() == 404) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(res.getResponseBody(), new TypeReference<>() {});
        } catch (Throwable t) {
            log.error("Error parsing response for show: {}", id, t);
            throw new RuntimeException(t);
        }
    }

    private HTTPResponseObject getFromTrakt(String path) {
        HTTPResponseObject response = new HTTPResponseObject();
        try {
            URL url = new URL(apiBaseUrl + path);

            HttpURLConnection con = getFullyReadyConnection(url, "GET");
            response = handleRequest(con);
        } catch (Throwable t) {
            log.error("Error with http request", t);
            log.error("HTTP response object: {}", response.toString());
            throw new TraktAPIException("Error with TraktAPI", t);
        }
        return response;
    }

    private HttpURLConnection getFullyReadyConnection(URL url, String method) throws Exception {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);

        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("trakt-api-version", apiVersion);
        con.setRequestProperty("trakt-api-key", apiKey);

        return con;
    }

    private HTTPResponseObject handleRequest(HttpURLConnection connection) throws Exception {
        log.debug("{} request: {}", connection.getRequestMethod(), connection.getURL());
        HTTPResponseObject response = new HTTPResponseObject();
        response.setStatus(connection.getResponseCode());
        log.debug("Response status: {}", response.getStatus());

        //reading response to string
        try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            response.setResponseBody(content.toString());
            log.debug("Response body: {}", response.getResponseBody());
        } catch (FileNotFoundException e) {
            log.debug("Empty response body");
        }
        connection.disconnect();

        return response;
    }

    static class HTTPResponseObject {
        private int status;
        private String responseBody;

        public int getStatus() {
            return status;
        }

        public String getResponseBody() {
            return responseBody;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }

        @Override
        public String toString() {
            return "HTTPResponseObject{" +
                    "status=" + status +
                    ", responseBody='" + responseBody + '\'' +
                    '}';
        }
    }
}
