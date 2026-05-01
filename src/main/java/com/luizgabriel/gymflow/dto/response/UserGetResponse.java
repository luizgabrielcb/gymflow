package com.luizgabriel.gymflow.dto.response;

import lombok.Data;

@Data
public class UserGetResponse {
    private Long id;
    private String name;
    private String email;
}
