package org.rentfriend.dto;

import java.sql.Date;

public record OfferDTO(Long id,
                       String title,
                       String description,
                       Double pricePerHour,
                       Date creationDate
                       ) {
}
