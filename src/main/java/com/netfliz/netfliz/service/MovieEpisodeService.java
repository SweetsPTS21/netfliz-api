package com.netfliz.netfliz.service;

import com.netfliz.netfliz.entity.MovieAssetEntity;
import com.netfliz.netfliz.entity.MovieEpisodeEntity;
import com.netfliz.netfliz.mapper.MovieAssetMapper;
import com.netfliz.netfliz.mapper.MovieEpisodeMapper;
import com.netfliz.netfliz.model.MovieEpisode;
import com.netfliz.netfliz.model.MovieEpisodePage;
import com.netfliz.netfliz.repository.MovieAssetRepository;
import com.netfliz.netfliz.repository.MovieEpisodeRepository;
import com.netfliz.netfliz.validator.MovieEpisodeValidator;
import com.netfliz.netfliz.validator.MovieValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MovieEpisodeService {
    private final MovieEpisodeRepository movieEpisodeRepository;
    private final MovieAssetRepository movieAssetRepository;
    private final MovieAssetMapper movieAssetMapper;
    private final MovieEpisodeMapper movieEpisodeMapper;
    private final MovieEpisodeValidator movieEpisodeValidator;
    private final MovieValidator movieValidator;

    public MovieEpisodePage getMovieEpisodes(Long movieId, Integer page, Integer pageSize) {
        movieValidator.validateSeriesMovie(movieId);

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<MovieEpisodeEntity> movieEpisodesPage = movieEpisodeRepository.findByMovieId(movieId, pageable);
        Map<Long, List<MovieAssetEntity>> mapAsset = movieAssetRepository.findByMovieId(movieId).stream()
                .collect(Collectors.groupingBy(MovieAssetEntity::getEpisodeId));

        List<MovieEpisode> movieEpisodes = movieEpisodesPage.getContent().stream().map(movieEpisodeEntity -> {
            List<MovieAssetEntity> assets = mapAsset.getOrDefault(movieEpisodeEntity.getId(), new ArrayList<>());
            return movieEpisodeMapper.mapFromEntity(movieEpisodeEntity, assets);
        }).toList();

        return buildPage(movieEpisodesPage, movieEpisodes);
    }

    public MovieEpisode updateMovieEpisode(Long movieId, MovieEpisode movieEpisode) {
        movieValidator.validateSeriesMovie(movieId);
        movieEpisodeValidator.validateEpisode(movieEpisode);

        // Lưu episode
        MovieEpisodeEntity movieEpisodeEntity = movieEpisodeRepository.save(movieEpisodeMapper.mapToEntity(movieId, movieEpisode));

        if (!CollectionUtils.isEmpty(movieEpisode.getAssets())) {
            List<MovieAssetEntity> movieAssetEntities = movieAssetMapper.mapToEntities(movieId, movieEpisodeEntity.getId(), movieEpisode.getAssets());
            return movieEpisodeMapper.mapFromEntity(
                    movieEpisodeEntity,
                    movieAssetRepository.saveAll(movieAssetEntities));
        }

        return movieEpisodeMapper.mapFromEntity(movieEpisodeEntity, new ArrayList<>());
    }

    private MovieEpisodePage buildPage(Page<MovieEpisodeEntity> entityPage,
                                       List<MovieEpisode> items) {
        MovieEpisodePage page = new MovieEpisodePage();
        page.setPage(entityPage.getNumber());
        page.setPageSize(entityPage.getSize());
        page.setTotal(entityPage.getTotalElements());
        page.setTotalPages(entityPage.getTotalPages());
        page.setItems(items);
        return page;
    }
}
