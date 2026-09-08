package com.yourname.blog.Blog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourname.blog.Blog.dto.CommentRequest;
import com.yourname.blog.Blog.entity.Blog;
import com.yourname.blog.Blog.entity.User;
import com.yourname.blog.Blog.repository.BlogRepository;
import com.yourname.blog.Blog.repository.BookmarkRepository;
import com.yourname.blog.Blog.repository.CommentRepository;
import com.yourname.blog.Blog.repository.LikeRepository;
import com.yourname.blog.Blog.repository.UserRepository;
import com.yourname.blog.Blog.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InteractionControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private Blog blog;
    private String token;

    @BeforeEach
    void setUp() {
        bookmarkRepository.deleteAll();
        likeRepository.deleteAll();
        commentRepository.deleteAll();
        blogRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(User.builder()
                .name("Interacting User")
                .email("user@example.com")
                .password("password")
                .role("USER")
                .isVerified(true)
                .createdAt(LocalDateTime.now())
                .build());

        blog = blogRepository.save(Blog.builder()
                .title("Sample Article")
                .content("Sample Content")
                .author(user)
                .build());

        token = jwtUtil.generateToken(user.getEmail());
    }

    @Test
    void testProfileEndpoint() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Interacting User"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void testCommentsEndpoints() throws Exception {
        CommentRequest req = new CommentRequest();
        req.setBlogId(blog.getId());
        req.setContent("Great article!");

        mockMvc.perform(post("/api/articles/" + blog.getId() + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Great article!"))
                .andExpect(jsonPath("$.authorName").value("Interacting User"));

        mockMvc.perform(get("/api/articles/" + blog.getId() + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Great article!"));
    }

    @Test
    void testLikesEndpoints() throws Exception {
        mockMvc.perform(post("/api/likes/" + blog.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Blog liked"));

        mockMvc.perform(get("/api/likes/" + blog.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        mockMvc.perform(post("/api/likes/" + blog.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Blog unliked"));
    }

    @Test
    void testBookmarksEndpoints() throws Exception {
        mockMvc.perform(post("/api/bookmarks/" + blog.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookmarked").value(true));

        mockMvc.perform(get("/api/bookmarks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(blog.getId()));

        mockMvc.perform(post("/api/bookmarks/" + blog.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookmarked").value(false));
    }
}
