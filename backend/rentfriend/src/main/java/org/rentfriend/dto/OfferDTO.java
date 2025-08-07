package org.rentfriend.dto;

public record OfferDTO(Long id,
                       String title,
                       String description,
                       Double pricePerHour
                       ) {
}
