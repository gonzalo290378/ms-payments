package com.bench.mspayments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;

    @JsonProperty("email")
    private String email;

    @JsonProperty("dni")
    private String dni;
}
