package com.netfliz.netfliz.service;

import com.netfliz.netfliz.entity.MovieAssetEntity;
import com.netfliz.netfliz.entity.MovieEpisodeEntity;
import com.netfliz.netfliz.entity.MovieImageEntity;
import com.netfliz.netfliz.entity.enums.MovieAssetType;
import com.netfliz.netfliz.entity.enums.MovieImageType;
import com.netfliz.netfliz.entity.enums.MovieObjectType;
import com.netfliz.netfliz.mapper.MovieAssetMapper;
import com.netfliz.netfliz.mapper.MovieEpisodeMapper;
import com.netfliz.netfliz.mapper.MovieImageMapper;
import com.netfliz.netfliz.model.MovieEpisode;
import com.netfliz.netfliz.model.MovieEpisodePage;
import com.netfliz.netfliz.model.SuggestEpisodeNumber;
import com.netfliz.netfliz.repository.MovieAssetRepository;
import com.netfliz.netfliz.repository.MovieEpisodeRepository;
import com.netfliz.netfliz.repository.MovieImageRepository;
import com.netfliz.netfliz.validator.MovieEpisodeValidator;
import com.netfliz.netfliz.validator.MovieValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final MovieImageRepository movieImageRepository;
    private final MovieImageMapper movieImageMapper;

    public MovieEpisodePage getMovieEpisodes(Long movieId, Integer page, Integer pageSize) {
        movieValidator.validateSeriesMovie(movieId);

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<MovieEpisodeEntity> movieEpisodesPage = movieEpisodeRepository.findByMovieId(movieId, pageable);
        List<Long> episodeIds = movieEpisodesPage.getContent().stream().map(MovieEpisodeEntity::getId).toList();

        Map<Long, List<MovieAssetEntity>> mapAsset = movieAssetRepository.findByObjectIds(episodeIds, MovieObjectType.EPISODE)
                .stream()
                .collect(Collectors.groupingBy(MovieAssetEntity::getObjectId));
        Map<Long, List<MovieImageEntity>> mapPoster = movieImageRepository
                .findByObjectIdsAndObjectType(episodeIds, MovieObjectType.EPISODE)
                .stream()
                .collect(Collectors.groupingBy(MovieImageEntity::getObjectId));

        List<MovieEpisode> movieEpisodes = movieEpisodesPage.getContent().stream().map(movieEpisodeEntity -> {
            List<MovieAssetEntity> assets = mapAsset.getOrDefault(movieEpisodeEntity.getId(), new ArrayList<>());
            List<MovieImageEntity> posters = mapPoster.getOrDefault(movieEpisodeEntity.getId(), new ArrayList<>());

            return movieEpisodeMapper.mapFromEntity(movieEpisodeEntity, assets, posters);
        }).toList();

        return buildPage(movieEpisodesPage, movieEpisodes);
    }

    @Transactional
    public MovieEpisode updateMovieEpisode(Long movieId, MovieEpisode movieEpisode) {
        movieValidator.validateSeriesMovie(movieId);
        movieEpisodeValidator.validateEpisode(movieId, movieEpisode);

        // Lưu episode
        MovieEpisodeEntity movieEpisodeEntity = movieEpisodeMapper.mapToEntity(movieId, movieEpisode);
        movieEpisodeRepository.save(movieEpisodeEntity);

        // Xóa toàn bộ poster/assets cũ
        movieImageRepository.deleteAllByObjectIdAndObjectTypeAndImageTypeIn(
                movieEpisodeEntity.getId(),
                MovieObjectType.EPISODE,
                List.of(MovieImageType.POSTER));
        movieAssetRepository.deleteAllByObjectId(
                movieEpisodeEntity.getId(),
                MovieObjectType.EPISODE,
                List.of(MovieAssetType.VIDEO, MovieAssetType.SUBTITLE));

        // Lưu posters
        List<MovieImageEntity> movieImageEntities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(movieEpisode.getPosters())) {
            movieImageEntities.addAll(movieImageMapper.mapToEntities(
                    movieEpisode.getPosters(),
                    movieEpisodeEntity.getId(),
                    MovieObjectType.EPISODE));
            movieImageRepository.saveAll(movieImageEntities);
        }

        // Lưu assets
        List<MovieAssetEntity> movieAssetEntities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(movieEpisode.getAssets())) {
            movieAssetEntities.addAll(movieAssetMapper.mapToEntities(
                    movieEpisodeEntity.getId(),
                    MovieObjectType.EPISODE,
                    movieEpisode.getAssets()));
            movieAssetRepository.saveAll(movieAssetEntities);
        }

        return movieEpisodeMapper.mapFromEntity(
                movieEpisodeEntity,
                movieAssetEntities,
                movieImageEntities);
    }

    /**
     * Suggest episode number and order
     *
     * @param movieId movieId
     * @return SuggestEpisodeNumber
     */
    public SuggestEpisodeNumber suggestEpisodeNumber(Long movieId) {
        Integer maxEpisodeNumber = movieEpisodeRepository.findMaxEpisodeNumberByMovieId(movieId).orElse(0);
        Integer maxEpisodeOrder = movieEpisodeRepository.findMaxEpisodeOrderByMovieId(movieId).orElse(0);

        SuggestEpisodeNumber suggest = new SuggestEpisodeNumber();
        suggest.setEpisodeNumber(maxEpisodeNumber + 1);
        suggest.setEpisodeOrder(maxEpisodeOrder + 1);

        return suggest;
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
