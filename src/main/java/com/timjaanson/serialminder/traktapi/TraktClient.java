package com.timjaanson.serialminder.traktapi;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timjaanson.serialminder.traktapi.exceptions.TraktAPIException;
import com.timjaanson.serialminder.traktapi.exceptions.TraktAPINotFoundException;
import com.timjaanson.serialminder.traktapi.traktResponse.TraktEpisode;
import com.timjaanson.serialminder.traktapi.traktResponse.TraktSeason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TraktClient implements InitializingBean {
    //TODO keep track of api request count and don't go over the limit
    private final Logger log = LoggerFactory.getLogger(TraktClient.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${trakt.api.key}")
    private String apiKey;

    @Value("${trakt.api.version:2}")
    private String apiVersion;

    @Value("${trakt.api.url:https://api.trakt.tv}")
    private String apiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private HttpEntity<String> requestEntity;


    @Override
    public void afterPropertiesSet() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("trakt-api-key", apiKey);
        headers.add("trakt-api-version", apiVersion);
        headers.setContentType(MediaType.APPLICATION_JSON);
        requestEntity = new HttpEntity<>("body", headers);
    }

    public List<TraktSeason> getSeasonsById(String id) {
        return getListFromTrakt(TraktSeason.class, "/shows/"+id+"/seasons");
    }

    public List<TraktSeason> getSeasonsAndEpisodesById(String id) {
        List<TraktSeason> seasons = getSeasonsById(id);
        seasons.forEach(season -> season.setEpisodes(getEpisodesByIdAndSeason(id, season.getNumber())));
        return seasons;
    }

    public List<TraktEpisode> getEpisodesByIdAndSeason(String id, Integer season) {
        try {
            return getListFromTrakt(TraktEpisode.class, "/shows/"+id+"/seasons/"+season);
        } catch (TraktAPINotFoundException e) {
            return new ArrayList<>();
        }
    }

    private <T> T getFromTrakt(Class<T> responseClass, String path) {
        ResponseEntity<String> responseEntity = get(apiBaseUrl + path);
        JavaType javaType = objectMapper.getTypeFactory().constructType(responseClass);
        return readValue(responseEntity, javaType);
    }

    private <T> List<T> getListFromTrakt(Class<T> responseClass, String path) {
        ResponseEntity<String> responseEntity = get(apiBaseUrl + path);
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, responseClass);
        return readValue(responseEntity, javaType);
    }

    private ResponseEntity<String> get(String url) {
        try {
            log.debug("GET request to {} {}", url, requestEntity);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            log.debug("Response: {}", responseEntity.toString());
            return responseEntity;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new TraktAPINotFoundException();
            }
            throw new TraktAPIException("Error with TraktAPI", e);
        }
    }

    private <T> T readValue(ResponseEntity<String> response, JavaType javaType) {
        T result = null;
        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
            try {
                result = objectMapper.readValue(response.getBody(), javaType);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return result;
    }
}
