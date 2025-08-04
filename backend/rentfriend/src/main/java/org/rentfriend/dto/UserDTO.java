package org.rentfriend.dto;

import org.rentfriend.entity.Profile;

public record UserDTO(String username, String email, String role, Profile profile) {
}
