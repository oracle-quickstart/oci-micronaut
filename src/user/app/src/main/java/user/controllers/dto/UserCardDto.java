package user.controllers.dto;

import io.micronaut.serde.annotation.Serdeable;

import jakarta.validation.constraints.Size;

/**
 * {@link user.model.UserCard} DTO.
 */
@Serdeable
public class UserCardDto {

    private final String ccv;

    @Size(min = 16, max = 16)
    private final String longNum;

    @Size(min = 4, max = 4)
    private final String expires;

    public UserCardDto(String ccv, String longNum, String expires) {
        this.ccv = ccv;
        this.longNum = longNum;
        this.expires = expires;
    }

    public String getCcv() {
        return ccv;
    }

    public String getLongNum() {
        return longNum;
    }

    public String getExpires() {
        return expires;
    }

    @Override
    public String toString() {
        return "UserCardDto{" +
                "ccv='" + ccv + '\'' +
                ", longNum='" + longNum + '\'' +
                ", expires='" + expires + '\'' +
                '}';
    }
}
