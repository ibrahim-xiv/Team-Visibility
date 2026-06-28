package com.TeamVisibility.App.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:jdbc:h2:file:./data/teamvisibility;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE}")
    private String databaseUrl;

    @Value("${DATABASE_USERNAME:sa}")
    private String username;

    @Value("${DATABASE_PASSWORD:}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Render gibt postgresql:// statt jdbc:postgresql:// — fix das automatisch
        String jdbcUrl = databaseUrl;
        if (jdbcUrl.startsWith("postgresql://")) {
            jdbcUrl = "jdbc:postgresql://" + jdbcUrl.substring("postgresql://".length());
        }

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);

        if (jdbcUrl.contains("h2")) {
            ds.setDriverClassName("org.h2.Driver");
            ds.setUsername("sa");
            ds.setPassword("");
        } else {
            ds.setDriverClassName("org.postgresql.Driver");
            ds.setUsername(username);
            ds.setPassword(password);
        }

        return ds;
    }
}