package springStripeEcommerceService.mainClasses.security.jwtfilter;

import springStripeEcommerceService.mainClasses.security.accountconfiguration.UserDetailsMapper;
import springStripeEcommerceService.mainClasses.security.accountconfiguration.UserDetailsMapperService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

	@Autowired
	private UserDetailsMapperService userDetailsMapperService;

	@Autowired
	public JwtAuthFilter(UserDetailsMapperService userDetailsMapperService){
		this.userDetailsMapperService = userDetailsMapperService;
	}

	private Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String token = null;

		if(authHeader != null && authHeader.startsWith("Bearer ")){
			token = authHeader.substring(7);
		}

		if(token!=null && JwtUtil.validateToken(token)){
			String username = JwtUtil.extractUsername(token);

			UserDetailsMapper userDetails = userDetailsMapperService.loadUserByUsername(username);
			Authentication auth = new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
			logger.info("jwt token {} retrieved in jwtAuthFilter", token);
		}else{
			logger.info("invalid token {} in jwtAuthFilter", token);
		}

		filterChain.doFilter(request, response);
	}
}
