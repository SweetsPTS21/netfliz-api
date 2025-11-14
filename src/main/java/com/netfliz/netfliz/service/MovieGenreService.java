package com.netfliz.netfliz.service;

import com.netfliz.netfliz.api.MovieGenresApiDelegate;
import com.netfliz.netfliz.constant.CacheKey;
import com.netfliz.netfliz.entity.MovieGenreEntity;
import com.netfliz.netfliz.mapper.MovieGenreMapper;
import com.netfliz.netfliz.model.MovieGenre;
import com.netfliz.netfliz.repository.MovieGenreRepository;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class MovieGenreService implements MovieGenresApiDelegate {
    private final MovieGenreRepository movieGenreRepository;
    private final MovieGenreMapper movieGenreMapper;
    private final RedisService redisService;

    @Override
    public ResponseEntity<List<MovieGenre>> getAllMovieGenres() {
        String cacheKey = CacheKey.buildKey(CacheKey.CACHE_MOVIE_GENRES);
        var cachedMovieGenreList = redisService.getList(cacheKey, MovieGenre.class);

        if (Objects.nonNull(cachedMovieGenreList)) {
            return ResponseEntity.ok(cachedMovieGenreList);
        }

        List<MovieGenreEntity> movieGenreEntityList = movieGenreRepository.findAll();
        List<MovieGenre> movieGenreList = movieGenreMapper.mapFromEntities(movieGenreEntityList);
        redisService.set(cacheKey, movieGenreList);

        return ResponseEntity.ok(movieGenreList);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieGenre> getMovieGenreById(Integer id) {
        MovieGenreEntity entity = movieGenreRepository.findById(id).orElseThrow(
                () -> new ValidationException("Movie genre not found")
        );

        MovieGenre movieGenre = movieGenreMapper.mapFromEntity(entity);
        return ResponseEntity.ok(movieGenre);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieGenre> createMovieGenre(MovieGenre movieGenre) {
        MovieGenreEntity entity = movieGenreMapper.mapToEntity(movieGenre);
        return ResponseEntity.ok(movieGenreMapper.mapFromEntity(movieGenreRepository.save(entity)));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieGenre> updateMovieGenreById(Integer id, MovieGenre movieGenre) {
        MovieGenreEntity entity = movieGenreRepository.findById(id).orElseThrow(
                () -> new ValidationException("Movie genre not found")
        );

        entity.setName(movieGenre.getName());
        entity.setTitle(movieGenre.getTitle());
        entity.setDescription(movieGenre.getDescription());
        entity.setSlug(movieGenre.getSlug());
        return ResponseEntity.ok(movieGenreMapper.mapFromEntity(movieGenreRepository.save(entity)));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deleteMovieGenreById(Integer id) {
        MovieGenreEntity entity = movieGenreRepository.findById(id).orElseThrow(
                () -> new ValidationException("Movie genre not found")
        );

        movieGenreRepository.delete(entity);
        return ResponseEntity.ok(true);
    }
}
