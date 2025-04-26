package adidasCopy.adidasCopyBackEnd.security;

import adidasCopy.adidasCopyBackEnd.security.accountconfiguration.UserDetailsMapperService;
import adidasCopy.adidasCopyBackEnd.security.jwtfilter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

	@Autowired
	UserDetailsMapperService userDetailsMapperService;
	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		return httpSecurity
				.securityMatcher("/**")
				.cors((cors) -> cors
						.configurationSource(corsConfigurationSource()))
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/getTree", "/checkOut", "/login", "/register").permitAll()
						.requestMatchers("/saveTree/**", "/saveTree", "/update").hasAuthority("ADMIN")
						.requestMatchers("/testAdminEndpoint").hasAuthority("ADMIN")
						.requestMatchers("/testOwnerEndpoint").hasAuthority("OWNER")
						.requestMatchers(
								"/addOrderToBasket",
								"/getBasketOrders" ,
								"/getClientSecret"
						).hasAnyAuthority("CUSTOMER", "OWNER", "ADMIN")
						.requestMatchers("/stripeWebHook").permitAll() // CHANGE THIS
						.anyRequest()
						.authenticated()
				)
				.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
				.build();

	}

	@Bean
	UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public JwtAuthFilter jwtAuthFilter(){return new JwtAuthFilter(userDetailsMapperService);}

	@Bean
	public AuthenticationManager authenticationManager(){
		System.out.println("entered authenticationManager");
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(userDetailsMapperService);
		return new ProviderManager(provider);
	}
}

