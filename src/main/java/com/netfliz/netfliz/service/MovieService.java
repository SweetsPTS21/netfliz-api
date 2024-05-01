package com.netfliz.netfliz.service;

import com.netfliz.netfliz.api.MoviesApiDelegate;
import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.mapper.MovieMapper;
import com.netfliz.netfliz.model.Movie;
import com.netfliz.netfliz.repository.IMovieRepository;
import com.netfliz.netfliz.validator.MovieValidator;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService implements MoviesApiDelegate {
    IMovieRepository movieRepository;

    private final MovieMapper movieMapper;
    private final MovieValidator movieValidator;
    private final MongoTemplate mongoTemplate;

    public MovieService(IMovieRepository movieRepository,MovieMapper movieMapper, MovieValidator movieValidator, MongoTemplate mongoTemplate) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
        this.movieValidator = movieValidator;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ResponseEntity<List<Movie>> getAllMovie(Integer page, Integer pageSize, String filter, String sort) {
        Criteria criteria = setFilterCriteria(new Criteria(), filter);

        MatchOperation match = Aggregation.match(criteria);
//        GroupOperation group = Aggregation.group().max("imdbRating").as("maxRating");
        SortOperation sorted = Aggregation.sort(Sort.by(Sort.Order.desc("imdbRating")));
        LimitOperation limit = Aggregation.limit(pageSize != null ? pageSize : 10);

        Aggregation aggregation = Aggregation.newAggregation(match, sorted, limit);
        List<MovieEntity> result = mongoTemplate.aggregate(aggregation, "movies", MovieEntity.class).getMappedResults();
        List<Movie> movies = movieMapper.mapMovieEntityListToMovieList(result);
        return ResponseEntity.ok(movies);
    }

    @Override
    public ResponseEntity<Movie> getMovieById(String movieId) {
        movieValidator.validateMovieExist(movieId);

        Optional<MovieEntity> movieOptional = movieRepository.findById(movieId);

        if (movieOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Movie movie = movieMapper.mapMovieEntityToMovie(movieRepository.findById(movieId).get());
        return ResponseEntity.ok(movie);
    }

    @Override
    public ResponseEntity<Movie> createMovie(Movie movie) {
        MovieEntity movieEntity = movieMapper.mapMovieToMovieEntity(movie);
        movieRepository.save(movieEntity);
        return ResponseEntity.ok(movie);
    }

    @Override
    public ResponseEntity<Void> updateMovie(String movieId, Movie movie) {
        movieValidator.validateMovieExist(movieId);

        Optional<MovieEntity> movieOptional = movieRepository.findById(movieId);

        if (movieOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MovieEntity movieEntity = movieMapper.mapMovieToMovieEntity(movie);
        movieEntity.setId(movieId);
        movieRepository.save(movieEntity);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteMovie(String movieId) {
        movieValidator.validateMovieExist(movieId);

        Optional<MovieEntity> movieOptional = movieRepository.findById(movieId);

        if (movieOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        movieRepository.deleteById(movieId);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<Movie>> bulkMovie(List<Movie> movies) {
        movies.forEach(movie -> {
            MovieEntity movieEntity = movieMapper.mapMovieToMovieEntity(movie);
            movieRepository.save(movieEntity);
        });

        return ResponseEntity.ok(movies);
    }

    public Criteria setFilterCriteria(Criteria criteria, String filter) {
        if (filter != null && !filter.isEmpty()) {
            String[] filterArray = filter.split(" ");
            String field =  Arrays.stream(filterArray).findFirst().get();
            String operator = Arrays.stream(filterArray).skip(1).findFirst().get();
            String value = Arrays.stream(filterArray).skip(2).findFirst().get();

            switch (operator) {
                case "eq":
                    criteria = Criteria.where(field).is(value);
                    break;
                case "gt":
                    criteria = Criteria.where(field).gt(value);
                    break;
                case "lt":
                    criteria = Criteria.where(field).lt(value);
                    break;
                case "gte":
                    criteria = Criteria.where(field).gte(value);
                    break;
                case "lte":
                    criteria = Criteria.where(field).lte(value);
                    break;
                case "ne":
                    criteria = Criteria.where(field).ne(value);
                    break;
                case "in":
                    criteria = Criteria.where(field).in(value);
                    break;
                case "nin":
                    criteria = Criteria.where(field).nin(value);
                    break;
                case "regex":
                    criteria = Criteria.where(field).regex(value);
                    break;
                default:
                    criteria = Criteria.where(field).is(value);
                    break;
            }
        }

        return criteria;
    }
}
