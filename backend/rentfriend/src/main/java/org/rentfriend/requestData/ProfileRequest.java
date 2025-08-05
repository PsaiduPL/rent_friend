package org.rentfriend.requestData;

import org.rentfriend.entity.BodyParameter;
import org.rentfriend.entity.Interest;

import java.util.List;

public record ProfileRequest(
    String name,
    String description,
    String city,
    Integer age,
    List<InterestRequest> interestList,
    BodyParameterRequest bodyParameter
) {
}
