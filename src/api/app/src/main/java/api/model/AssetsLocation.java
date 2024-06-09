package api.model;
import io.micronaut.serde.annotation.Serdeable;
@Serdeable
public record AssetsLocation (String productImagePath){}