package com.example.ex02.config;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.ex02.security.CustomUserDetailService;
import com.example.ex02.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private final MemberService memberService;
	
	@Autowired
	public SecurityConfig(MemberService memberService) {
		this.memberService = memberService;
	}

	@Bean
	public WebSecurityCustomizer configure() {
		return web -> web.ignoring()
				.requestMatchers("/static/**");
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of("http://localhost:5173"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Authorization-refresh", "Cache-Control", "Content-Type"));
		configuration.setAllowCredentials(true);
//		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public UserDetailsService CustomUserService() {
        return new CustomUserDetailService();
    }
	
	@Bean
	public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {	
//		http.authenticationManager(authenticationManager(http));
        http.csrf(csrf -> csrf
        		.disable());
        
        http.cors(cors -> cors
        		.configurationSource(corsConfigurationSource()));
        
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS, "/**")
				.permitAll()
				.requestMatchers("/api/board/**", "/api/member/**", "/api/reply/**"
						, "/api/file/**", "/api/item/**", "/api/cart/**", "/api/order/**"
						, "/api/payment/**", "/api/admin/**")
				.permitAll()
				.anyRequest()
				.authenticated()
				);
		
		http.formLogin(login -> login
				.loginProcessingUrl("/api/member/login")
				.usernameParameter("id")
				.passwordParameter("password")
				.successHandler(customSuccessHandler())
				.failureHandler(customFailureHandler())
				.permitAll());
		
		http.logout(logout -> logout
				.logoutUrl("/api/member/logout")
				.logoutSuccessHandler(customLogoutSuccessHandler())
				.invalidateHttpSession(true));
		
		http.exceptionHandling(exception -> exception
				.authenticationEntryPoint(customauthenticationEntryPoint())
				.accessDeniedHandler(customaccessDeniedHandler()));
		
//		http.sessionManagement((session) -> session
//            .sessionFixation((sessionFixation) -> sessionFixation
//            .newSession()));
		
		return http.build();
	}
	
	
	private HttpServletResponse response(HttpServletResponse response, Map<String, Object> map, int status) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String result = objectMapper.writeValueAsString(map);
			
			if (status != 200) {
				for (String key : map.keySet()) {
					String value = (String) map.get(key);
					log.info(key + " : " + value);
				}
			}
			
			response.setCharacterEncoding("UTF-8");
			response.setStatus(status);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.getWriter().write(result);
			response.getWriter().flush();
			response.getWriter().close();
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	@Bean
	public AccessDeniedHandler customaccessDeniedHandler() {
		return new AccessDeniedHandler() {
			@Override
			public void handle(HttpServletRequest request, HttpServletResponse response,
					AccessDeniedException accessDeniedException) throws IOException, ServletException {
				
				Map<String, Object> map = new HashMap<>();
				map.put("message", HttpStatus.UNAUTHORIZED);
				
				response(response, map, HttpServletResponse.SC_UNAUTHORIZED);
				
			}
		};
	}
	
	@Bean
	public AuthenticationEntryPoint customauthenticationEntryPoint() {
		return new AuthenticationEntryPoint() {
			@Override
			public void commence(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException authException) throws IOException, ServletException {
				
				Map<String, Object> map = new HashMap<>();
				map.put("message", HttpStatus.UNAUTHORIZED);
				
				response(response, map, HttpServletResponse.SC_UNAUTHORIZED);
			}
		};
	}

	@Bean
	public LogoutSuccessHandler customLogoutSuccessHandler() {
		return new LogoutSuccessHandler() {
			@Override
			public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				
				Map<String, Object> map = new HashMap<>();
				map.put("message", "ok");
				
				response(response, map, HttpServletResponse.SC_OK);
			}
		};
	}

	@Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
    	 return new AuthenticationSuccessHandler() {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				
				WebAuthenticationDetails web = (WebAuthenticationDetails) authentication.getDetails();
				Map<String, Object> map = new HashMap<>();
				
				String id = authentication.getName();
				String sessionId = web.getSessionId();
				String role = authentication.getAuthorities()
						.stream()
						.map(GrantedAuthority::getAuthority)
						.collect(Collectors.joining());
				
				role = role.substring(role.indexOf("_") + 1, role.length());
				
				LocalDate today = LocalDate.now();
//				String formatDate = today.format(formatter);
				
				if (!role.equals("ADMIN")) {
					int cnt = memberService.getVisit(today);
					
					if (cnt == 0) {
						memberService.insertVisit(today);
						
					} else {
						memberService.updateVisit(today);
					}
				}
				
				map.put("id", id);
				map.put("sessionId", sessionId);
				map.put("role", role);
				
				response(response, map, HttpServletResponse.SC_OK);
			}
    	 };
    }
    
    @Bean
    public AuthenticationFailureHandler customFailureHandler() {
		return new AuthenticationFailureHandler() {
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				String id = request.getParameter("id");
				String password = request.getParameter("password");
				
				Map<String, Object> map = new HashMap<>();
				String message;
				
				if (!StringUtils.hasText(id)) {
					message = "아이디 필수";
					
				} else if (!StringUtils.hasText(password)) {
					message = "비밀번호 필수";
					
				} else if (exception instanceof BadCredentialsException) {
					message = "아이디 또는 비밀번호 불일치";
					
				} else {
					message = "시스템 오류";
				}
				
				map.put("message", message);
				
				response(response, map, HttpStatus.UNAUTHORIZED.value());
			}
		};
    }
	
//	@Bean
//	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//		AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//		authenticationManagerBuilder.userDetailsService(CustomUserService()).passwordEncoder(passwordEncoder());
//		AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
//		
//		return authenticationManager;
//		
//		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//		authenticationProvider.setUserDetailsService(CustomUserService());
//		authenticationProvider.setPasswordEncoder(passwordEncoder());
//		authenticationProvider.setHideUserNotFoundExceptions(false);
//		
//		ProviderManager providerManager = new ProviderManager(authenticationProvider);
//		return providerManager;
//	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(CustomUserService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//		daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
		
		ProviderManager providerManager = new ProviderManager(daoAuthenticationProvider);
		return daoAuthenticationProvider;
	}
	
	@Bean
	public RoleHierarchy roleHierarchy() {
		return RoleHierarchyImpl
				.withDefaultRolePrefix()
				.role("ADMIN").implies("MEMBER")
				.build();
	}
}
