package user.api.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * {@link user.model.UserCard} detailed DTO.
 */
@Serdeable
public class UserCardDetailDto {

    private final UUID id;
    private final String number;
    private final String longNum;
    private final String expires;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
    private final UserDetailDto user;

    public UserCardDetailDto(UUID id,
                             String number,
                             String longNum,
                             String expires,
                             OffsetDateTime createdAt,
                             OffsetDateTime updatedAt,
                             @Nullable UserDetailDto user) {
        this.id = id;
        this.number = number;
        this.longNum = longNum;
        this.expires = expires;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getLongNum() {
        return longNum;
    }

    public String getExpires() {
        return expires;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UserDetailDto getUser() {
        return user;
    }
}
