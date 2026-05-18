package com.budget.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

/**
 * Configure Jackson pour convertir les objets Java en JSON
 * 
 * JavaTimeModule → permet de sérialiser LocalDate, LocalDateTime
 * Sans ça, Jackson ne sait pas convertir les dates Java 8+
 * 
 * WRITE_DATES_AS_TIMESTAMPS → écrit les dates en format lisible
 * ex: "2024-01-15" au lieu de [2024, 1, 15]
 */
@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper;

    public JacksonConfig() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}