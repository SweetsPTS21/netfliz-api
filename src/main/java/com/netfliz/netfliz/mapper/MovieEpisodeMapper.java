package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.MovieAssetEntity;
import com.netfliz.netfliz.entity.MovieEpisodeEntity;
import com.netfliz.netfliz.entity.MovieImageEntity;
import com.netfliz.netfliz.model.MovieEpisode;
import com.netfliz.netfliz.util.JsonUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class MovieEpisodeMapper {
    private final MovieAssetMapper movieAssetMapper;
    private final MovieImageMapper movieImageMapper;

    public MovieEpisode mapFromEntity(MovieEpisodeEntity from,
                                      List<MovieAssetEntity> assetEntities,
                                      List<MovieImageEntity> posterEntities) {
        MovieEpisode to = new MovieEpisode();

        to.setId(from.getId());
        to.setName(from.getName());
        to.setEpisodeNumber(from.getEpisodeNumber());
        to.setEpisodeOrder(from.getEpisodeOrder());
        to.setDescription(from.getDescription());
        to.setIsPublished(from.getIsPublished());
        to.setRuntime(from.getRuntime());
        to.setMetadata(JsonUtils.serialize(from.getMetadata()));
        to.setAssets(movieAssetMapper.mapFromEntities(assetEntities));
        to.setPosters(movieImageMapper.mapFromEntities(posterEntities));

        return to;
    }

    public MovieEpisodeEntity mapToEntity(Long movieId, MovieEpisode from) {
        MovieEpisodeEntity to = new MovieEpisodeEntity();

        if (Objects.nonNull(from.getId())) {
            to.setId(from.getId());
        }
        to.setMovieId(movieId);
        to.setName(from.getName());
        to.setEpisodeNumber(from.getEpisodeNumber());
        to.setEpisodeOrder(from.getEpisodeOrder());
        to.setDescription(from.getDescription());
        to.setIsPublished(from.getIsPublished());
        to.setRuntime(from.getRuntime());
        to.setMetadata(JsonUtils.parse(from.getMetadata()));

        return to;
    }
}
