package com.example.securitytest.config;

import com.example.securitytest.config.jwt.JwtAuthenticationFilter;
import com.example.securitytest.config.jwt.JwtAuthorizationFilter;
import com.example.securitytest.config.oauth.PrincipalOauthUserService;
import com.example.securitytest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)   // 특정 주소로 접근을 하면 권한 및 인증을 미리 체크하겠다는 뜻.
public class SecurtiyConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PrincipalOauthUserService principalOauthUserService;

	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				// 토큰 기반 인증이므로 세션도 사용 x
			.and()
//			.addFilter(new JwtAuthenticationFilter(authenticationManager()))
//			.addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
				.authorizeRequests()
				.antMatchers("/user/**").authenticated()
				.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
				.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
				.anyRequest().permitAll()
			.and()
				.addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JwtAuthorizationFilter(authenticationManager(), userRepository), BasicAuthenticationFilter.class)
				.formLogin()
				.loginPage("/loginForm")
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/")
			.and()
				.oauth2Login()
				.loginPage("/loginForm")
				.userInfoEndpoint()
				.userService(principalOauthUserService)

		;

	}
	
}
