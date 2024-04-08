package com.vicarius.quota;

import com.vicarius.quota.model.UserQuota;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@EnableJpaRepositories
@ComponentScan(basePackages = "com.vicarius.quota.*")
public class QuotaApplicationStarter {

	@Bean
	@Scope("singleton")
	public ConcurrentHashMap<Long, UserQuota> userQuotaCache() {
		return new ConcurrentHashMap<>();
	}

	public static void main(String[] args) {
		SpringApplication.run(QuotaApplicationStarter.class, args);
	}
}