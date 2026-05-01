package com.kra.api.infrastructure.web.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record ProjectMetadataResponse(String role, String year, String kind, String mainBranch, List<String> stack)
        implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
