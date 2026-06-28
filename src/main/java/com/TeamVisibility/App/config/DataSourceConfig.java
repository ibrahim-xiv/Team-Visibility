package com.TeamVisibility.App.config;

import java.net.URI;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() throws Exception {
        HikariDataSource ds = new HikariDataSource();

        if (databaseUrl == null || databaseUrl.isEmpty() || databaseUrl.startsWith("jdbc:h2")) {
            // Lokal: H2
            ds.setJdbcUrl("jdbc:h2:file:./data/teamvisibility;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE");
            ds.setDriverClassName("org.h2.Driver");
            ds.setUsername("sa");
            ds.setPassword("");
        } else {
            // Render: postgresql://user:password@host/dbname
            URI uri = new URI(databaseUrl.replace("postgresql://", "http://"));
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 5432 : uri.getPort();
            String path = uri.getPath(); // /dbname
            String userInfo = uri.getUserInfo(); // user:password
            String user = userInfo.split(":")[0];
            String pass = userInfo.split(":")[1];

            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;

            ds.setJdbcUrl(jdbcUrl);
            ds.setUsername(user);
            ds.setPassword(pass);
            ds.setDriverClassName("org.postgresql.Driver");
        }

        return ds;
    }
}