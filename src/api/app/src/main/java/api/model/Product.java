package api.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Product (String id, Double price){}

