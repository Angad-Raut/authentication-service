package com.projectx.authentication_service.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequest {
    @NotNull(message = "{username.not,null}")
    private String username;
    @NotNull(message = "{password.not.null}")
    @Size(min = 7,max = 15,message = "{password.size}")
    private String password;
}
