package com.sparta.lv1_spring.dto;

import com.sparta.lv1_spring.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String username;
    private String contents;
    private LocalDateTime createdat;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.username = post.getUsername();
        this.contents = post.getContents();
        this.createdat = post.getCreatedat();
    }

    public PostResponseDto(Long id, String title, String username, String contents, LocalDateTime createdat) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.contents = contents;
        this.createdat = createdat;
    }
}
