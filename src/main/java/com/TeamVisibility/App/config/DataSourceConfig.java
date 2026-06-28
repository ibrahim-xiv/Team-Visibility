package com.TeamVisibility.App.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Value("${DATABASE_USERNAME:sa}")
    private String username;

    @Value("${DATABASE_PASSWORD:}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() throws Exception {
        HikariDataSource ds = new HikariDataSource();

        if (databaseUrl == null || databaseUrl.isEmpty() || databaseUrl.startsWith("jdbc:h2")) {
            ds.setJdbcUrl("jdbc:h2:mem:nachbarschaftdb");
            ds.setDriverClassName("org.h2.Driver");
            ds.setUsername("sa");
            ds.setPassword("");
        } else {
            URI uri = new URI(databaseUrl.replace("postgresql://", "http://"));
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 5432 : uri.getPort();
            String path = uri.getPath();
            String userInfo = uri.getUserInfo();
            String user = userInfo.split(":")[0];
            String pass = userInfo.split(":")[1];

            ds.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + path);
            ds.setUsername(user);
            ds.setPassword(pass);
            ds.setDriverClassName("org.postgresql.Driver");
        }

        return ds;
    }
}
