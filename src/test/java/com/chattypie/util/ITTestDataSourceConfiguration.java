package com.chattypie.util;

import static com.chattypie.util.ITDatabaseUtils.readTestDatabaseUrl;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * An alternative datasource configuration module meant for providing a {@link javax.sql.DataSource}
 * pointing to a local IT database.
 */
@Configuration
@EnableAutoConfiguration // Enables Flyway to kick in automagically and migrate the database, same as when the real app starts
@Slf4j
public class ITTestDataSourceConfiguration {

	@Bean
	public MysqlDataSource testDatasource() throws IOException {

		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		String databaseUrl = readTestDatabaseUrl();
		log.info("Connecting to test DB at {}", databaseUrl);
		mysqlDataSource.setURL(databaseUrl);
		mysqlDataSource.setUser("root");
		mysqlDataSource.setPassword("password");
		return mysqlDataSource;
	}

	@Bean
	public TestDatabaseHandle testDatabaseHandle(MysqlDataSource itTestDatasource) {
		return new TestDatabaseHandle(itTestDatasource.getUrl());
	}
}