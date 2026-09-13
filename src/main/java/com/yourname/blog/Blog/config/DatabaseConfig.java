package com.yourname.blog.Blog.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Slf4j
@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url:}")
    private String rawUrl;

    @Value("${spring.datasource.username:}")
    private String rawUsername;

    @Value("${spring.datasource.password:}")
    private String rawPassword;

    @Value("${spring.datasource.driver-class-name:}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        String url = rawUrl != null ? rawUrl.trim() : "";
        String username = rawUsername != null ? rawUsername.trim() : "";
        String password = rawPassword != null ? rawPassword.trim() : "";

        // Normalize Render / Heroku postgres:// or postgresql:// URLs into JDBC format
        if (url.startsWith("postgres://") || url.startsWith("postgresql://")) {
            try {
                // Temporarily replace protocol with http to parse userinfo, host, port, path, query
                String httpEquivalent = url.replaceFirst("^postgres(ql)?://", "http://");
                URI uri = new URI(httpEquivalent);

                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath(); // e.g. /dbname
                String query = uri.getQuery();

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                if (query != null && !query.isEmpty()) {
                    jdbcUrl += "?" + query;
                }
                url = jdbcUrl;

                if (uri.getUserInfo() != null) {
                    String[] userInfo = uri.getUserInfo().split(":", 2);
                    if (username.isEmpty()) {
                        username = userInfo[0];
                    }
                    if (userInfo.length > 1 && password.isEmpty()) {
                        password = userInfo[1];
                    }
                }
                log.info("Normalized PostgreSQL connection string for Render deployment: host={}, port={}, db={}", host, port, path);
            } catch (Exception e) {
                log.warn("Could not parse postgres connection URI, using raw URL: {}", e.getMessage());
            }
        }

        // Fallback for local dev or tests when no database is provided
        if (url.isEmpty()) {
            log.info("No database URL provided; falling back to in-memory H2 database");
            url = "jdbc:h2:mem:thoughtnest;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
            username = "sa";
            password = "";
            driverClassName = "org.h2.Driver";
        }

        dataSource.setJdbcUrl(url);
        if (!username.isEmpty()) {
            dataSource.setUsername(username);
        }
        dataSource.setPassword(password);

        if (driverClassName != null && !driverClassName.trim().isEmpty()) {
            dataSource.setDriverClassName(driverClassName.trim());
        } else if (url.startsWith("jdbc:postgresql:")) {
            dataSource.setDriverClassName("org.postgresql.Driver");
        } else if (url.startsWith("jdbc:h2:")) {
            dataSource.setDriverClassName("org.h2.Driver");
        }

        // Sensible connection pool defaults suitable for containerized and free cloud tiers
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);

        return dataSource;
    }
}
