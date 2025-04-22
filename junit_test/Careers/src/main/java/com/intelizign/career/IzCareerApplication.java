package com.intelizign.career;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.intelizign.career.utils.PageSerializer;

@EnableJpaAuditing
@SpringBootApplication
public class IzCareerApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(IzCareerApplication.class, args);
	}
	
	/*
	 * Configuration for JSON View to work on Page Response
	 */
	@Bean
	public SimpleModule jacksonPageWithJsonViewModule() {
		SimpleModule module = new SimpleModule("jackson-page-with-jsonview", Version.unknownVersion());
		module.addSerializer(PageImpl.class, new PageSerializer());
		return module;
	}
}
