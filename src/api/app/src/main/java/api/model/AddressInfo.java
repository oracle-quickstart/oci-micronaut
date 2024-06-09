package api.model;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
@Schema(title="User address",description="User address details.")
@Serdeable
public record AddressInfo(
        @Schema(title="Address id",example="22")
        @Nullable String id,
        @Schema(title="Street number",example="2")
        @NotEmpty String number,
        @Schema(title="Street name",example="Round the corner")
        @NotEmpty String street,
        @Schema(title="Street name",example="Lanai")
        @NotEmpty String city,
        @Schema(title="Country",example="Hawaii")
        @NotEmpty String country,
        @Schema(title="Postcode",example="96763")
        @NotEmpty String postcode){



    public static AddressInfo createWithoutId(@NotEmpty String number,
                                              @NotEmpty String street,
                                              @NotEmpty String city,
                                              @NotEmpty String country,
                                              @NotEmpty String postcode) {

        return new AddressInfo(null, number, street, city, country, postcode);

    }
}

