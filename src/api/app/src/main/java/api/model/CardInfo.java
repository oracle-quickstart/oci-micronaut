package api.model;

import io.micronaut.core.annotation.Creator;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Optional;

@Schema(title="Payment card", description="User payment card details.")
@Serdeable
public record CardInfo(
        @Schema(title="Card id.", example="22")
        @Nullable String id,
        @Schema(title="Card ccv.", example="359")
        @Nullable String ccv,
        @Schema(title="Card number.", example="111122223333444")
        @Size(min=16,max=16) String longNum,
        @Schema(title="Card expiration.", example="0426")
        @Size(min=4,max=4) String expires
){
    public static CardInfo createWithoutId(@NotEmpty String ccv,
                                              @NotEmpty String longNum,
                                              @NotEmpty String expires) {

        return new CardInfo(null, ccv, longNum, expires);

    }
}