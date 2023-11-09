package com.sparta.lv1_spring.repository;

import com.sparta.lv1_spring.dto.PostRequestDto;
import com.sparta.lv1_spring.dto.PostResponseDto;
import com.sparta.lv1_spring.entity.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PostRepository {
    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }


    public Post save(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO post (title,username,password,contents,created_at) VALUES (?, ?, ? ,?, ?)";
        jdbcTemplate.update(con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    preparedStatement.setString(1, post.getTitle());
                    preparedStatement.setString(2, post.getUsername());
                    preparedStatement.setString(3, post.getPassword());
                    preparedStatement.setString(4, post.getContents());
                    preparedStatement.setTimestamp(5, Timestamp.valueOf(post.getCreatedAt()));
                    return preparedStatement;
                },
                keyHolder);

        Long id = keyHolder.getKey().longValue();
        post.setId(id);

        return post;
    }

    public List<PostResponseDto> findAll() {
        // DB 조회
        String sql = "SELECT * FROM post order By created_at desc";

        return jdbcTemplate.query(sql, new RowMapper<PostResponseDto>() {
            @Override
            public PostResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("id");
                String username = rs.getString("username");
                String contents = rs.getString("contents");
                String title = rs.getString("title");
                LocalDateTime createdat = rs.getTimestamp("created_at").toLocalDateTime();
                return new PostResponseDto(id, title, username, contents, createdat);
            }
        });
    }

    public Post findById(Long id) {
        String sql = "SELECT * FROM post WHERE id = ?";

        return jdbcTemplate.query(sql, resultSet -> {
            if(resultSet.next()) {
                Post post = new Post();
                post.setId(resultSet.getLong("id"));
                post.setUsername(resultSet.getString("username"));
                post.setPassword(resultSet.getString("password"));
                post.setContents(resultSet.getString("contents"));
                post.setTitle(resultSet.getString("title"));
                post.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                return post;
            } else {
                return null;
            }
        }, id);
    }

    public void update(Long id, PostRequestDto requestDto) {
        String sql = "UPDATE post SET title =?, username = ?, contents = ? WHERE id = ?";
        jdbcTemplate.update(sql, requestDto.getTitle(), requestDto.getUsername(), requestDto.getContents(), id);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM post WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
