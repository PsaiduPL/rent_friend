package org.rentfriend.requestData;

import jakarta.validation.constraints.*;

public record OfferRequest(
    @NotNull()
    @NotBlank
    @Size(min = 1, max = 250)
    String title,
    @NotNull
    @NotBlank
    @Size(min = 10, max = 2500)
    String description,
    @Min(1)
    @Max(99999)
    Double pricePerHour) {
}
