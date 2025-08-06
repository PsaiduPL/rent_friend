package org.rentfriend.entity;

public record OfferDTO(Long id,
                       String title,
                       String description,
                       Double pricePerHour
                       ) {
}
