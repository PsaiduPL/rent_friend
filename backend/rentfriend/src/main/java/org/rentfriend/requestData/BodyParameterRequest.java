package org.rentfriend.requestData;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record BodyParameterRequest(
    @Min(120)
    @Max(250)
    Double height
    ,
    @Min(30)
    @Max(300)
    Double weight) {
}
