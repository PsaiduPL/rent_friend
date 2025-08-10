package org.rentfriend.dto;

import java.util.List;

public record ProfileDTO(Long id,
                         String role,
                         String name,
                         Integer age,
                         String city,
                         String description,
                         String gender,
                         BodyParameterDTO bodyParameter,
                         List<InterestDTO> interestList,
                         ImageDTO profilePicture) {
}
