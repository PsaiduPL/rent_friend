package org.rentfriend.preview;

import org.rentfriend.dto.InterestDTO;

import java.util.List;

public record ProfilePreview(Long id,
                             String role,
                             String name,
                             Integer age,
                             String city,
                             String gender,
                             List<InterestDTO> top3InterestsList,
                             List<OfferPreview> top3offerPreviewList) {
}
