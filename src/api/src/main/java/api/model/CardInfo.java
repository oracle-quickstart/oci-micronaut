package api.model;

import io.micronaut.core.annotation.Creator;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Optional;

@Introspected
public class CardInfo {
    @Nullable
    private final String id;

    @Nullable
    private final String ccv;

    @Size(min = 16, max = 16)
    private final String longNum;

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
