package org.project.pack.configuration;

import org.project.pack.services.UDS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration implements WebMvcConfigurer{
	
	@Value("${csrf.ignore.path}")
	String csrf_ignore;
	@Value("${login.page.path}")
	String login_page;
	@Value("${login.process.path}")
	String login_process;
	@Value("${login.username}")
	String username;
	@Value("${login.password}")
	String password;
	@Value("${login.success.path}")
	String login_success;
	@Value("${login.failure.path}")
	String login_failure;
	@Value("${login.logout.path}")
	String login_logout;
	@Value("${login.logout.redirect.path}")
	String login_logout_redirect;
	
	@Autowired
	UDS userdetailsservice;
	
	@Bean
	public SecurityFilterChain register(HttpSecurity http) throws Exception {
		http
			.csrf(target->
				target
					.ignoringRequestMatchers(csrf_ignore)
			)
			.authorizeHttpRequests(target->
				target
					.dispatcherTypeMatchers(DispatcherType.FORWARD)
					.permitAll()
					.requestMatchers("/")
					.authenticated()
					.requestMatchers("/app/**","/api/**")
					.hasAnyRole("USER")
					.anyRequest()
					.permitAll()
			)
			.logout(target->
				target
					.logoutUrl(login_logout)
					.clearAuthentication(true)
					.deleteCookies("JSESSIONID")
					.invalidateHttpSession(true)
					.logoutSuccessUrl(login_logout_redirect)
			)
			.oauth2Login(target->
				target
					.userInfoEndpoint(endpointTarget->
						endpointTarget
							.userService(userdetailsservice)
					)
					.defaultSuccessUrl(login_success)
					.loginPage(login_page)
					.failureUrl(login_failure)
					.permitAll()
			)
			;
		
		return http.getOrBuild();
	}
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:9999") // 클라이언트의 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
























