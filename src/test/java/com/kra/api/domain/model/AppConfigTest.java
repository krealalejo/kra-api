package com.kra.api.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void testConstructorAndGetters() {
        AppConfig config = new AppConfig("home.jpg", "cv.pdf");
        assertEquals("home.jpg", config.getHomePortraitUrl());
        assertEquals("cv.pdf", config.getCvPortraitUrl());
    }

    @Test
    void testDefaultConstructorAndSetters() {
        AppConfig config = new AppConfig();
        config.setHomePortraitUrl("new_home.jpg");
        config.setCvPortraitUrl("new_cv.pdf");
        assertEquals("new_home.jpg", config.getHomePortraitUrl());
        assertEquals("new_cv.pdf", config.getCvPortraitUrl());
    }
}
