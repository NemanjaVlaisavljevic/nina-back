package com.nemanjav.back.dto;

import com.nemanjav.back.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class UserDto {

    @NotEmpty
    @NotBlank
    private String email;

    @NotEmpty
    @Size(min = 3, message = "Length must be more than 3")
    private String password;

    @NotEmpty
    @NotNull
    @NotBlank
    private String firstName;

    @NotEmpty
    @NotNull
    @NotBlank
    private String lastName;

    @NotEmpty
    private String phone;

    @NotEmpty
    private String city;

    @NotEmpty
    private String streetAndNumber;

    private UserRole userRole;
}
