package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.MovieMetadata;
import com.netfliz.netfliz.service.MovieMetadataService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MovieMetadataController implements MovieMetadataApi {
    private final MovieMetadataService movieMetadataService;

    @Override
    public ResponseEntity<MovieMetadata> getMovieMetadata() {
        return movieMetadataService.getMovieMetadata();
    }
}
