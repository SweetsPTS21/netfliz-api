package com.netfliz.netfliz.api.graphql;

import com.netfliz.netfliz.model.MovieEpisode;
import com.netfliz.netfliz.model.MovieEpisodePage;
import com.netfliz.netfliz.model.SuggestEpisodeNumber;
import com.netfliz.netfliz.service.MovieEpisodeService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class MovieEpisodeGraphql {
    private final MovieEpisodeService movieEpisodeService;

    @QueryMapping
    public MovieEpisodePage getMovieEpisodes(@Argument Long movieId, @Argument Integer page, @Argument Integer pageSize) {
        return movieEpisodeService.getMovieEpisodes(movieId, page, pageSize);
    }

    @QueryMapping
    public SuggestEpisodeNumber suggestEpisodeNumber(@Argument Long movieId) {
        return movieEpisodeService.suggestEpisodeNumber(movieId);
    }

    @MutationMapping
    public MovieEpisode updateMovieEpisode(@Argument Long movieId, @Argument MovieEpisode movieEpisode) {
        return movieEpisodeService.updateMovieEpisode(movieId, movieEpisode);
    }
}
