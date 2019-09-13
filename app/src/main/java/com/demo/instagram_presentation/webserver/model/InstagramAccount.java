package com.demo.instagram_presentation.webserver.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InstagramAccount {
    private String username;
    private String password;
}
