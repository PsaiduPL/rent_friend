package org.rentfriend.dto;

import java.util.List;

public record ProfileDetailsDTO(ProfileDTO profile, List<OfferDTO> offers) {
}
