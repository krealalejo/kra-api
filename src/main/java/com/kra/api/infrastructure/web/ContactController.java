package com.kra.api.infrastructure.web;

import com.kra.api.application.ContactService;
import com.kra.api.infrastructure.web.dto.ContactAcceptedResponse;
import com.kra.api.infrastructure.web.dto.CreateContactRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/contact")
    public ResponseEntity<ContactAcceptedResponse> submit(@Valid @RequestBody CreateContactRequest request) {
        String id = contactService.submitLead(request.getEmail(), request.getMessage());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ContactAcceptedResponse("accepted", id));
    }
}
