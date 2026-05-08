package com.kra.api.infrastructure.web.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateDtoTest {

    @Test
    void updateBlogPostRequest_gettersAndSetters() {
        UpdateBlogPostRequest req = new UpdateBlogPostRequest();
        req.setTitle("My Title");
        req.setContent("My Content");
        req.setReferences(List.of());
        req.setImageUrl("https://example.com/img.png");

        assertThat(req.getTitle()).isEqualTo("My Title");
        assertThat(req.getContent()).isEqualTo("My Content");
        assertThat(req.getReferences()).isEmpty();
        assertThat(req.getImageUrl()).isEqualTo("https://example.com/img.png");
    }

    @Test
    void updateEducationRequest_gettersAndSetters() {
        UpdateEducationRequest req = new UpdateEducationRequest();
        req.setTitle("BSc CS");
        req.setInstitution("MIT");
        req.setLocation("Cambridge");
        req.setYears("2019-2023");
        req.setDescription("Graduated top of class");
        req.setSortOrder(1);

        assertThat(req.getTitle()).isEqualTo("BSc CS");
        assertThat(req.getInstitution()).isEqualTo("MIT");
        assertThat(req.getLocation()).isEqualTo("Cambridge");
        assertThat(req.getYears()).isEqualTo("2019-2023");
        assertThat(req.getDescription()).isEqualTo("Graduated top of class");
        assertThat(req.getSortOrder()).isEqualTo(1);
    }

    @Test
    void updateExperienceRequest_gettersAndSetters() {
        UpdateExperienceRequest req = new UpdateExperienceRequest();
        req.setTitle("Software Engineer");
        req.setCompany("Acme");
        req.setLocation("London");
        req.setYears("2020-2023");
        req.setDescription("Backend dev");
        req.setSortOrder(2);

        assertThat(req.getTitle()).isEqualTo("Software Engineer");
        assertThat(req.getCompany()).isEqualTo("Acme");
        assertThat(req.getLocation()).isEqualTo("London");
        assertThat(req.getYears()).isEqualTo("2020-2023");
        assertThat(req.getDescription()).isEqualTo("Backend dev");
        assertThat(req.getSortOrder()).isEqualTo(2);
    }

    @Test
    void updateSkillCategoryRequest_gettersAndSetters() {
        UpdateSkillCategoryRequest req = new UpdateSkillCategoryRequest();
        req.setName("Backend");
        req.setSkills(List.of("Java", "Spring"));
        req.setSortOrder(3);

        assertThat(req.getName()).isEqualTo("Backend");
        assertThat(req.getSkills()).containsExactly("Java", "Spring");
        assertThat(req.getSortOrder()).isEqualTo(3);
    }

    @Test
    void updateProfileRequest_gettersAndSetters() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setHomePortraitUrl("images/portrait.jpg");
        req.setCvPortraitUrl("images/cv.jpg");
        req.setCvPdfUrl("documents/cv.pdf");

        assertThat(req.getHomePortraitUrl()).isEqualTo("images/portrait.jpg");
        assertThat(req.getCvPortraitUrl()).isEqualTo("images/cv.jpg");
        assertThat(req.getCvPdfUrl()).isEqualTo("documents/cv.pdf");
    }

    @Test
    void uploadRequest_gettersAndSetters() {
        UploadRequest req = new UploadRequest();
        req.setFilename("photo.jpg");
        req.setContentType("image/jpeg");
        req.setUploadType("portrait");

        assertThat(req.getFilename()).isEqualTo("photo.jpg");
        assertThat(req.getContentType()).isEqualTo("image/jpeg");
        assertThat(req.getUploadType()).isEqualTo("portrait");
    }
}
