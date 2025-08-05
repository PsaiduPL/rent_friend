package org.rentfriend.dto;

public record ProfileDTO(Long id,
                         String role,
                         String name,
                         Integer age,
                         String city,
                         String description,
                         BodyParameterDTO bodyParameter) {
}
