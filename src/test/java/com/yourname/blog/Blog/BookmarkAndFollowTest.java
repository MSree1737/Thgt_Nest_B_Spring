package com.yourname.blog.Blog;

import com.yourname.blog.Blog.entity.Blog;
import com.yourname.blog.Blog.entity.User;
import com.yourname.blog.Blog.repository.BlogRepository;
import com.yourname.blog.Blog.repository.BookmarkRepository;
import com.yourname.blog.Blog.repository.FollowRepository;
import com.yourname.blog.Blog.repository.UserRepository;
import com.yourname.blog.Blog.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookmarkAndFollowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private User user1;
    private User user2;
    private String token1;
    private String token2;
    private Blog blogBy2;

    @BeforeEach
    void setUp() {
        bookmarkRepository.deleteAll();
        followRepository.deleteAll();
        blogRepository.deleteAll();
        userRepository.deleteAll();

        user1 = userRepository.save(User.builder().name("Alice").email("alice@test.com").password("pwd").role("ROLE_USER").isVerified(true).build());
        user2 = userRepository.save(User.builder().name("Bob").email("bob@test.com").password("pwd").role("ROLE_USER").isVerified(true).build());

        token1 = jwtUtil.generateToken(user1.getEmail());
        token2 = jwtUtil.generateToken(user2.getEmail());

        blogBy2 = blogRepository.save(Blog.builder().title("Bob's Story").content("Interesting content").author(user2).build());
    }

    @Test
    void toggleBookmarkSavesAndRemovesCleanly() throws Exception {
        // First toggle: save
        mockMvc.perform(post("/api/bookmarks/" + blogBy2.getId())
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookmarked").value(true));

        // Fetch my bookmarks
        mockMvc.perform(get("/api/bookmarks")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(blogBy2.getId()))
                .andExpect(jsonPath("$[0].bookmarked").value(true));

        // Second toggle: remove
        mockMvc.perform(post("/api/bookmarks/" + blogBy2.getId())
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookmarked").value(false));

        // Fetch my bookmarks: should be empty
        mockMvc.perform(get("/api/bookmarks")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void toggleFollowUpdatesStatusAndFollowingFeed() throws Exception {
        // Alice cannot follow herself
        mockMvc.perform(post("/api/follows/" + user1.getId())
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isBadRequest());

        // Following feed initially empty
        mockMvc.perform(get("/api/articles/following")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        // Alice follows Bob
        mockMvc.perform(post("/api/follows/" + user2.getId())
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.following").value(true))
                .andExpect(jsonPath("$.followersCount").value(1));

        // Following feed now contains Bob's story
        mockMvc.perform(get("/api/articles/following")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].authorName").value("Bob"))
                .andExpect(jsonPath("$.content[0].authorFollowedByCurrentUser").value(true));

        // Unfollow Bob
        mockMvc.perform(post("/api/follows/" + user2.getId())
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.following").value(false))
                .andExpect(jsonPath("$.followersCount").value(0));
    }
}
