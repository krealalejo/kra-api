package com.kra.api.infrastructure.web.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record ActivityCardResponse(String type, String title, String description, List<String> tags, String url)
        implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
