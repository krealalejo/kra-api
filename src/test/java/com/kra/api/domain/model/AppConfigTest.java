package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void testConstructorAndGetters() {
        AppConfig config = new AppConfig("home.jpg", "cv.jpg", "documents/cv.pdf");
        assertEquals("home.jpg", config.getHomePortraitUrl());
        assertEquals("cv.jpg", config.getCvPortraitUrl());
        assertEquals("documents/cv.pdf", config.getCvPdfUrl());
    }

    @Test
    void testDefaultConstructorAndSetters() {
        AppConfig config = new AppConfig();
        config.setHomePortraitUrl("new_home.jpg");
        config.setCvPortraitUrl("new_cv.jpg");
        config.setCvPdfUrl("documents/new_cv.pdf");
        assertEquals("new_home.jpg", config.getHomePortraitUrl());
        assertEquals("new_cv.jpg", config.getCvPortraitUrl());
        assertEquals("documents/new_cv.pdf", config.getCvPdfUrl());
    }
}
