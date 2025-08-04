package org.rentfriend.requestData;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.logging.log4j.message.Message;

public record MyUserRequest(
                            @NotNull(message = "username null")
                            @NotBlank(message = "username cannot be blank")
                            @Size( max = 50, message = "username max 50 characters")
                            String username,

                            @NotNull(message = "password null")
                            @NotBlank(message = "password cannot be blank")
                            @Size( max = 20, message = "password max 20 characters")
                            String password,

                            @NotNull(message = "email null")
                            @NotBlank(message = "email cannot be blank")
                            @Size(max = 200 , message = "email max 200 characters")
                            @Email(message = "invalid email") String email) {
}
