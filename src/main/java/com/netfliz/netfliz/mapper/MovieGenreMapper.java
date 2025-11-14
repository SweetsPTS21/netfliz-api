package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.MovieGenreEntity;
import com.netfliz.netfliz.model.MovieGenre;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieGenreMapper {

    public MovieGenre mapFromEntity(MovieGenreEntity from) {
        MovieGenre to = new MovieGenre();
        to.setId(from.getId());
        to.setName(from.getName());
        to.setTitle(from.getTitle());
        to.setDescription(from.getDescription());
        to.setSlug(from.getSlug());
        return to;
    }

    public MovieGenreEntity mapToEntity(MovieGenre from) {
        MovieGenreEntity to = new MovieGenreEntity();
        to.setId(from.getId());
        to.setName(from.getName());
        to.setTitle(from.getTitle());
        to.setDescription(from.getDescription());
        to.setSlug(from.getSlug());
        return to;
    }

    public List<MovieGenre> mapFromEntities(List<MovieGenreEntity> entityList) {
        return entityList.stream().map(this::mapFromEntity).toList();
    }
}
