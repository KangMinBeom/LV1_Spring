package com.sparta.lv1_spring.service;

import com.sparta.lv1_spring.dto.PostRequestDto;
import com.sparta.lv1_spring.dto.PostResponseDto;
import com.sparta.lv1_spring.entity.Post;
import com.sparta.lv1_spring.repository.PostRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public PostResponseDto createPost(PostRequestDto requestDto) {
        // RequestDto -> Entity
        Post post = new Post(requestDto);

        Post savedPost = postRepository.save(post);

        // Entity -> ResponseDto
        PostResponseDto postResponseDto = new PostResponseDto(post);

        return postResponseDto;

    }

    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id);
        if (post != null) {
            return new PostResponseDto(post.getId(), post.getTitle(), post.getUsername(), post.getContents(), post.getCreatedat());
        } else {
            throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다.");
        }
    }

    public List<PostResponseDto> getPosts() {
        return postRepository.findAll();
    }

    public PostResponseDto updatePost(Long id,PostRequestDto requestDto) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = postRepository.findById(id);
        if (post != null) {
            // memo 내용 수정
            if (post.getPassword().equals(requestDto.getPassword())){
                postRepository.update(id,requestDto);
                return new PostResponseDto(post.getId(),requestDto.getTitle(),requestDto.getUsername(),requestDto.getContents(),post.getCreatedat());
            }
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.");
        }
        return null;
    }

    public Long deletePost(Long id, PostRequestDto requestDto) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = postRepository.findById(id);
        if(post != null) {
            if (post.getPassword().equals(requestDto.getPassword())){
                postRepository.delete(id);
                return id;
            }

        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.");
        }
        return null;
    }
}
