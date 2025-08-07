package org.rentfriend.requestData;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Range;
import org.rentfriend.entity.BodyParameter;
import org.rentfriend.entity.Interest;

import java.util.List;

public record ProfileRequest(
    @NotNull(message = "name null")
    @NotBlank(message = "name cannot be blank")
    @Size(max = 50,message = "name max 50 characters")
    String name,

    @NotNull
    @NotBlank(message = "description cannot be empty")
    @Size(max = 1500,message="description max 1500 characters")
    String description,

    @NotNull
    @NotBlank(message = "city cannot be empty")
    @Size(max = 150)
    String city,

    @Min(18)
    @Max(100)
    Integer age,

    @NotNull
    @NotBlank
    @Size(max = 50)
    String gender,

    @NotNull(message = "interests cannot be empty")
    @NotEmpty
    List<InterestRequest> interestList,
    BodyParameterRequest bodyParameter
) {
}
