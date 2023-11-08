package com.sparta.lv1_spring.controller;

import com.sparta.lv1_spring.dto.PostRequestDto;
import com.sparta.lv1_spring.dto.PostResponseDto;
import com.sparta.lv1_spring.entity.Post;
import org.springframework.cglib.core.Local;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class PostController {
    private final JdbcTemplate jdbcTemplate;

    public PostController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/post")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto) {
        // RequestDto -> Entity
        Post post = new Post(requestDto);

        // DB 저장
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본 키를 반환받기 위한 객체

        String sql = "INSERT INTO post (title,username,password,contents,created_at) VALUES (?, ?, ? ,?, ?)";
        jdbcTemplate.update(con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    preparedStatement.setString(1, post.getTitle());
                    preparedStatement.setString(2, post.getUsername());
                    preparedStatement.setString(3, post.getPassword());
                    preparedStatement.setString(4, post.getContents());
                    preparedStatement.setTimestamp(5, Timestamp.valueOf(post.getCreatedat()));
                    return preparedStatement;
                },
                keyHolder);

        // DB Insert 후 받아온 기본키 확인
        Long id = keyHolder.getKey().longValue();
        post.setId(id);

        // Entity -> ResponseDtoz
        PostResponseDto postResponseDto = new PostResponseDto(post);

        return postResponseDto;
    }

    @GetMapping("/post/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        Post post = findById(id);
        if (post != null) {
            return new PostResponseDto(post.getId(), post.getTitle(), post.getUsername(), post.getContents(), post.getCreatedat());
        } else {
            throw new IllegalArgumentException("선택한 메모는 존재하지 않습니다.");
        }
    }

    @GetMapping("/posts")
    public List<PostResponseDto> getPosts() {
        // DB 조회
        String sql = "SELECT * FROM post order By created_at desc";

        return jdbcTemplate.query(sql, new RowMapper<PostResponseDto>() {
            @Override
            public PostResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                // SQL 의 결과로 받아온 Memo 데이터들을 MemoResponseDto 타입으로 변환해줄 메서드
                Long id = rs.getLong("id");
                String username = rs.getString("username");
                String contents = rs.getString("contents");
                String title = rs.getString("title");
                LocalDateTime createdat = rs.getTimestamp("created_at").toLocalDateTime();
                return new PostResponseDto(id, title, username, contents, createdat);
            }
        });
    }

    @PutMapping("/post/{id}")
    public PostResponseDto updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = findById(id);
        if (post != null) {
            // memo 내용 수정
            if (post.getPassword().equals(requestDto.getPassword())){
                String sql = "UPDATE post SET title =?, username = ?, contents = ? WHERE id = ?";
                jdbcTemplate.update(sql, requestDto.getTitle(), requestDto.getUsername(), requestDto.getContents(), id);
                return new PostResponseDto(post.getId(),post.getTitle(),post.getUsername(),post.getContents(),post.getCreatedat());
            }
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.");
        }
        return null;
    }

    @DeleteMapping("/post/{id}")
    public String deletePost(@PathVariable Long id) {
        // 해당 메모가 DB에 존재하는지 확인
        Post post = findById(id);
        if(post != null) {
            String sql = "DELETE FROM post WHERE id = ?";
            jdbcTemplate.update(sql, id);
            return "삭제되었습니다";
        } else {
            throw new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.");
        }
    }

    private Post findById(Long id) {
        // DB 조회
        String sql = "SELECT * FROM post WHERE id = ?";

        return jdbcTemplate.query(sql, resultSet -> {
            if(resultSet.next()) {
                Post post = new Post();
                post.setId(resultSet.getLong("id"));
                post.setUsername(resultSet.getString("username"));
                post.setPassword(resultSet.getString("password"));
                post.setContents(resultSet.getString("contents"));
                post.setTitle(resultSet.getString("title"));
                post.setCreatedat(resultSet.getTimestamp("created_at").toLocalDateTime());
                return post;
            } else {
                return null;
            }
        }, id);
    }
}
