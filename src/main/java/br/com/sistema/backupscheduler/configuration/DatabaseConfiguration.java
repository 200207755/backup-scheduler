package br.com.sistema.backupscheduler.configuration;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories(basePackages = "br.com.sistema.backupscheduler.repository", entityManagerFactoryRef = "entityManager")
public class DatabaseConfiguration implements EnvironmentAware {

	private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);
	private DataSourceProperties dsProps;

	@Bean(name = "baseDataSource")
	public DataSource baseDataSource() {
		/*return DataSourceBuilder.create()
				.url(dsProps.getUrl())
				.username(dsProps.getUsername())
				.password(dsProps.getPassword())
				.driverClassName(dsProps.getDriverClassName()).build();*/
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(dsProps.getDriverClassName());
		config.setJdbcUrl(dsProps.getUrl());
		config.setUsername(dsProps.getUsername());
		config.setPassword(dsProps.getPassword());
		config.setMinimumIdle(0);
		return new HikariDataSource(config);

	}

	public void setEnvironment(Environment environment) {
		dsProps = Binder.get(environment).bind("spring.datasource.*", DataSourceProperties.class).orElse(null);
	}

	@Bean
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManager(EntityManagerFactoryBuilder builder,DataSource dataSource) {
		log.info("Configuring EntityManagerFactory for Tenants");
		LocalContainerEntityManagerFactoryBean factory = builder.dataSource(dataSource).persistenceUnit("default").jta(true).packages("br.com.sistema").build();
		factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		factory.setJpaDialect(new HibernateJpaDialect());
		return factory;
	}

}