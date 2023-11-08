package com.sparta.lv1_spring.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostRequestDto {
    private String title;
    private String username;
    private String password;
    private String contents;
//    private LocalDateTime createdat;
}
