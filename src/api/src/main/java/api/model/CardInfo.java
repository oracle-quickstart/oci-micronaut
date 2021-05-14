package api.model;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Optional;

@Schema(title = "Payment card", description = "User payment card details.")
@Introspected
public class CardInfo {
    @Schema(title = "Card id.", example = "22")
    @Nullable
    private final String id;

    @Schema(title = "Card ccv.", example = "359")
    @Nullable
    private final String ccv;

    @Schema(title = "Card number.", example = "111122223333444")
    @Size(min = 16, max = 16)
    private final String longNum;

    @Schema(title = "Card expiration.", example = "0426")
    @Size(min = 4, max = 4)
    private final String expires;

    @Creator
    public CardInfo(@Nullable String id, @Nullable String ccv, String longNum, String expires) {
        this.id = id;
        this.ccv = ccv;
        this.longNum = longNum;
        this.expires = expires;
    }

    public CardInfo(@Nullable String ccv, String longNum, String expires) {
        this(null, ccv, longNum, expires);
    }

    @Nullable
    public Optional<String> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<String> getCcv() {
        return Optional.ofNullable(ccv);
    }

    public String getLongNum() {
        return longNum;
    }

    public String getExpires() {
        return expires;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardInfo cardInfo = (CardInfo) o;
        return longNum.equals(cardInfo.longNum) && expires.equals(cardInfo.expires);
    }

    @Override
    public int hashCode() {
        return Objects.hash(longNum, expires);
    }
}
