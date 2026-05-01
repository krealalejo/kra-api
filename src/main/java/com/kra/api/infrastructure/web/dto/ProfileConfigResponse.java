package com.kra.api.infrastructure.web.dto;

import java.io.Serial;
import java.io.Serializable;

public record ProfileConfigResponse(String homePortraitUrl, String cvPortraitUrl, String cvPdfUrl)
        implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
