package com.netfliz.netfliz.service;

import com.netfliz.netfliz.api.MovieMetadataApiDelegate;
import com.netfliz.netfliz.constant.CacheKey;
import com.netfliz.netfliz.entity.enums.MovieCountry;
import com.netfliz.netfliz.entity.enums.MovieLanguage;
import com.netfliz.netfliz.entity.enums.MovieRated;
import com.netfliz.netfliz.entity.enums.MovieType;
import com.netfliz.netfliz.model.Metadata;
import com.netfliz.netfliz.model.MovieGenre;
import com.netfliz.netfliz.model.MovieMetadata;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MovieMetadataService implements MovieMetadataApiDelegate {
    private final MovieGenreService movieGenreService;
    private final RedisService redisService;
    private static final List<Integer> DEFAULT_YEARS = List.of(2025, 2024, 2023, 2022, 2021, 2020, 2019,
            2018, 2017, 2016, 2015, 2014, 2013, 2012, 2011, 2010);

    @Override
    public ResponseEntity<MovieMetadata> getMovieMetadata() {
        String cacheKey = CacheKey.buildKey(CacheKey.CACHE_MOVIE_METADATA);
//        var cachedMetadata = redisService.get(cacheKey, MovieMetadata.class);
//        if (Objects.nonNull(cachedMetadata)) {
//            return ResponseEntity.ok(cachedMetadata);
//        }

        MovieMetadata metadata = new MovieMetadata();

        List<MovieGenre> genreList = Optional.ofNullable(movieGenreService.getAllMovieGenres().getBody()).orElse(new ArrayList<>());
        List<Metadata> genres = genreList.stream()
                .map(genre -> buildMetadata(genre.getName(), genre.getTitle()))
                .toList();
        metadata.setGenres(genres);

        List<Metadata> countries = Arrays.stream(MovieCountry.values())
                .map(country -> buildMetadata(country.getValue(), country.getDescription()))
                .toList();
        metadata.setCountries(countries);

        List<Metadata> languages = Arrays.stream(MovieLanguage.values())
                .map(language -> buildMetadata(language.getValue(), language.getDescription()))
                .toList();
        metadata.setLanguages(languages);

        List<Metadata> rated = Arrays.stream(MovieRated.values())
                .map(rate -> buildMetadata(rate.getValue(), rate.getDescription()))
                .toList();
        metadata.setRated(rated);

        List<Metadata> types = Arrays.stream(MovieType.values())
                .map(type -> buildMetadata(type.getValue(), type.getDescription()))
                .toList();
        metadata.setType(types);
        metadata.setYear(DEFAULT_YEARS.stream()
                .map(year -> buildMetadata(String.valueOf(year), String.valueOf(year)))
                .toList());

        // Cache metadata
        redisService.set(cacheKey, metadata);

        return ResponseEntity.ok(metadata);
    }

    private Metadata buildMetadata(String value, String description) {
        Metadata metadata = new Metadata();
        metadata.setValue(value);
        metadata.setDescription(description);
        metadata.setSlug(buildSlug(value));
        return metadata;
    }

    private static String buildSlug(String value) {
        return value.toLowerCase().replace(" ", "-");
    }
}
