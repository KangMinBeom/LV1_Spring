package com.sparta.lv1_spring.service;

import com.sparta.lv1_spring.dto.PostRequestDto;
import com.sparta.lv1_spring.dto.PostResponseDto;
import com.sparta.lv1_spring.entity.Post;
import com.sparta.lv1_spring.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

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

        post = postRepository.save(post);


        return new PostResponseDto(post);

    }

    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id);
        if (post != null) {
            return new PostResponseDto(post.getId(), post.getTitle(), post.getUsername(), post.getContents(), post.getCreatedAt());
        }
        throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.");
    }

    public List<PostResponseDto> getPosts() {
        return postRepository.findAll();
    }

    public PostResponseDto updatePost(Long id, PostRequestDto requestDto) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = postRepository.findById(id);
        if (post != null) {
            // memo 내용 수정
            if (StringUtils.equals(post.getPassword(), requestDto.getPassword())) {
                postRepository.update(id, requestDto);
                return new PostResponseDto(post.getId(), requestDto.getTitle(), requestDto.getUsername(), requestDto.getContents(), post.getCreatedAt());
            }
            return null;
        }
        throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.");
    }

    public Long deletePost(Long id, PostRequestDto requestDto) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = postRepository.findById(id);
        if (post != null) {
            if (StringUtils.equals(post.getPassword(),requestDto.getPassword())) {
                postRepository.delete(id);
                return id;
            }
            return null;
        }
        throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.");
    }
}
