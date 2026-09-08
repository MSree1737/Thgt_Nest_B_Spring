package com.yourname.blog.Blog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourname.blog.Blog.dto.BlogRequest;
import com.yourname.blog.Blog.entity.Blog;
import com.yourname.blog.Blog.entity.User;
import com.yourname.blog.Blog.repository.BlogRepository;
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
class BlogAndArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user1;
    private User user2;
    private String token1;
    private String token2;

    @BeforeEach
    void setUp() {
        blogRepository.deleteAll();
        userRepository.deleteAll();

        user1 = userRepository.save(User.builder()
                .name("Author One")
                .email("author1@example.com")
                .password("password")
                .role("USER")
                .isVerified(true)
                .createdAt(LocalDateTime.now())
                .build());

        user2 = userRepository.save(User.builder()
                .name("Author Two")
                .email("author2@example.com")
                .password("password")
                .role("USER")
                .isVerified(true)
                .createdAt(LocalDateTime.now())
                .build());

        token1 = jwtUtil.generateToken(user1.getEmail());
        token2 = jwtUtil.generateToken(user2.getEmail());
    }

    @Test
    void testArticleCreationFeedEditAndDelete() throws Exception {
        // 1. Create Article as User 1
        BlogRequest createReq = new BlogRequest();
        createReq.setTitle("First Article Title");
        createReq.setContent("This is the detailed content of the first article.");

        String responseStr = mockMvc.perform(post("/api/articles")
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("First Article Title"))
                .andExpect(jsonPath("$.authorName").value("Author One"))
                .andReturn().getResponse().getContentAsString();

        Blog createdBlog = blogRepository.findAll().get(0);
        Long blogId = createdBlog.getId();

        // 2. Fetch Feed
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(blogId))
                .andExpect(jsonPath("$.content[0].title").value("First Article Title"));

        // 3. Fetch Single Article
        mockMvc.perform(get("/api/articles/" + blogId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(blogId))
                .andExpect(jsonPath("$.title").value("First Article Title"));

        // 4. Search Article
        mockMvc.perform(get("/api/articles/search?q=First"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("First Article Title"));

        // 5. Edit Article by non-owner (User 2) should fail with 403 Forbidden
        BlogRequest updateReq = new BlogRequest();
        updateReq.setTitle("Unauthorized Edit Title");
        updateReq.setContent("Unauthorized Edit Content");

        mockMvc.perform(put("/api/articles/" + blogId)
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isForbidden());

        // 6. Edit Article by owner (User 1)
        updateReq.setTitle("Updated Article Title");
        updateReq.setContent("Updated content of the first article.");

        mockMvc.perform(put("/api/articles/" + blogId)
                        .header("Authorization", "Bearer " + token1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Article Title"));

        // 7. Delete Article by owner (User 1)
        mockMvc.perform(delete("/api/articles/" + blogId)
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isNoContent());

        // 8. Confirm article deleted
        mockMvc.perform(get("/api/articles/" + blogId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testLegacyBlogControllerEndpoints() throws Exception {
        Blog blog = Blog.builder()
                .title("Legacy Blog")
                .content("Legacy Content")
                .author(user1)
                .build();
        blog = blogRepository.save(blog);

        mockMvc.perform(get("/api/blogs/" + blog.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Legacy Blog"));

        mockMvc.perform(get("/api/blogs/mine")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Legacy Blog"));
    }
}
