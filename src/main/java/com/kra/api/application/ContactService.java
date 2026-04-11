package com.kra.api.application;

import com.kra.api.domain.model.Lead;
import com.kra.api.domain.repository.LeadRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ContactService {

    private final LeadRepository leadRepository;

    public ContactService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public String submitLead(String email, String message) {
        String id = UUID.randomUUID().toString();
        Lead lead = new Lead(id, email, message, Instant.now());
        leadRepository.save(lead);
        return id;
    }
}
