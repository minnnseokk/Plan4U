package org.project.pack.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfiguration {

	@Value("${swagger.setting.title}")
	String title;
	@Value("${swagger.setting.description}")
	String description;
	@Value("${swagger.setting.version}")
	String version;
	@Value("${swagger.setting.email}")
	String email;
	@Value("${swagger.setting.name}")
	String name;
	@Value("${swagger.setting.url}")
	String url;
	@Value("${swagger.license.name}")
	String license_name;
	@Value("${swagger.license.url}")
	String license_url;
	
	@Bean
	public OpenAPI swaggersetting() {
		return new OpenAPI()
			.components(new Components())
			.info(
				new Info()
					.title(title)
					.description(description)
					.version(version)
					.contact(
						new Contact()
							.email(email)
							.name(name)
							.url(url)
					)
					.license(
						new License()
							.name(license_name)
							.url(license_url)
					)
			)
		;
	}
}
