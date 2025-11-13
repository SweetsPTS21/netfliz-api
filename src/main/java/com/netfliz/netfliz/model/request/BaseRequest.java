package com.netfliz.netfliz.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public abstract class BaseRequest {
    @Schema(description = "Page number", defaultValue = "0")
    private Integer page;

    @Schema(description = "Page size", defaultValue = "20")
    private  Integer pageSize;

    public void normalize() {
        if (page == null) {
            page = 0;
        }

        if (page > 0) {
            page = page - 1;
        }

        if (pageSize == null) {
            pageSize = 20;
        }
    }
}
