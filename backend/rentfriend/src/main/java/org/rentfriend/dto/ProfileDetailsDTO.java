package org.rentfriend.dto;

import org.rentfriend.entity.Offer;

import java.util.List;

public record ProfileDetailsDTO(ProfileDTO profile, List<Offer> offers) {
}
