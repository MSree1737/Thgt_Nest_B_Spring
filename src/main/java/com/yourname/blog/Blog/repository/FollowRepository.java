package com.yourname.blog.Blog.repository;

import com.yourname.blog.Blog.entity.Follow;
import com.yourname.blog.Blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);

    boolean existsByFollowerAndFollowed(User follower, User followed);

    long countByFollowed(User followed);

    long countByFollower(User follower);

    List<Follow> findByFollower(User follower);

    List<Follow> findByFollowed(User followed);
}
