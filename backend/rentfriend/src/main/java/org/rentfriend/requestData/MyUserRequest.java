package org.rentfriend.requestData;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MyUserRequest(@NotBlank
                            @Size(min = 1, max = 50)
                            String username,
                            @NotBlank
                            @Size(min = 1, max = 20)
                            String password,

                            @Email String email) {
}
