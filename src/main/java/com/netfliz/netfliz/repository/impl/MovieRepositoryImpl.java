package com.netfliz.netfliz.repository.impl;

import com.netfliz.netfliz.entity.MovieEntity;
import com.netfliz.netfliz.model.request.MovieFilterRequest;
import com.netfliz.netfliz.repository.CustomMovieRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class MovieRepositoryImpl implements CustomMovieRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Page<MovieEntity> findByGenres(String[] genres, Pageable pageable) {
        String sql = "SELECT * FROM movies WHERE jsonb_exists_any(genre, :genres)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("genres", new SqlParameterValue(Types.ARRAY, "text", genres));

        // Count
        String countSql = "SELECT COUNT(*) FROM movies WHERE jsonb_exists_any(genre, :genres)";
        Long total = Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class)).orElse(0L);

        // Search
        params.addValue("pageSize", pageable.getPageSize());
        params.addValue("offset", pageable.getOffset());
        List<MovieEntity> result = namedParameterJdbcTemplate.query(sql, params, rowMapper);

        return new PageImpl<>(result, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), total);
    }

    @Override
    public Page<MovieEntity> findByFilter(MovieFilterRequest request) {
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (!CollectionUtils.isEmpty(request.getGenres())) {
            whereClause.append(" AND jsonb_exists_any(genre, :genres)");
            String[] genres = request.getGenres().toArray(new String[0]);
            params.addValue("genres", new SqlParameterValue(Types.ARRAY, "text", genres));
        }

        if (!CollectionUtils.isEmpty(request.getCountries())) {
            whereClause.append(" AND country IN (:countries)");
            params.addValue("countries", request.getCountries());
        }

        if (!CollectionUtils.isEmpty(request.getLanguages())) {
            whereClause.append(" AND languages IN (:languages)");
            params.addValue("languages", request.getLanguages());
        }

        if (Strings.isNotBlank(request.getYear())) {
            whereClause.append(" AND year = :year");
            params.addValue("year", Integer.parseInt(request.getYear()));
        }

        if (Strings.isNotBlank(request.getRating())) {
            whereClause.append(" AND imdb_rating >= :imdb_rating");
            params.addValue("imdb_rating", request.getRating());
        }

        if (Strings.isNotBlank(request.getRated())) {
            whereClause.append(" AND rated = :rated");
            params.addValue("rated", request.getRated());
        }

        if (Strings.isNotBlank(request.getType())) {
            whereClause.append(" AND type = :type");
            params.addValue("type", request.getType());
        }

        // Count
        String countSql = "SELECT COUNT(*) FROM movies" + whereClause;
        Long total = Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class)).orElse(0L);

        // Search
        if (Strings.isNotBlank(request.getSort())) {
            whereClause.append(" ORDER BY :sort DESC");
            params.addValue("sort", request.getSort());
        } else {
            whereClause.append(" ORDER BY updated_at DESC");
        }

        whereClause.append(" LIMIT :limit OFFSET :offset");
        params.addValue("limit", request.getPageSize());
        params.addValue("offset", request.getPage() * request.getPageSize());

        String searchSql = "SELECT * FROM movies" + whereClause;
        List<MovieEntity> result = namedParameterJdbcTemplate.query(searchSql, params, rowMapper);

        return new PageImpl<>(result, PageRequest.of(request.getPage(), request.getPageSize()), total);
    }

    private final RowMapper<MovieEntity> rowMapper = (rs, rowNum) -> {
        MovieEntity m = new MovieEntity();
        m.setId(rs.getLong("id"));
        m.setTitle(rs.getString("title"));
        m.setGenre(rs.getString("genre"));
        m.setYear(rs.getInt("year"));
        m.setTrailer(rs.getString("trailer"));
        m.setRated(rs.getString("rated"));
        m.setReleased(rs.getString("released"));
        m.setRuntime(rs.getString("runtime"));
        m.setDirector(rs.getString("director"));
        m.setWriter(rs.getString("writer"));
        m.setActors(rs.getString("actors"));
        m.setPlot(rs.getString("plot"));
        m.setLanguages(rs.getString("languages"));
        m.setCountry(rs.getString("country"));
        m.setAwards(rs.getString("awards"));
        m.setPosterId(rs.getInt("poster_id"));
        m.setMetaScore(rs.getLong("meta_score"));
        m.setImdbRating(rs.getString("imdb_rating"));
        m.setImdbVotes(rs.getLong("imdb_votes"));
        m.setType(rs.getString("type"));
        m.setResponse(rs.getBoolean("response"));
        m.setImages(rs.getString("images"));
        m.setCategories(rs.getString("categories"));

        return m;
    };
}
