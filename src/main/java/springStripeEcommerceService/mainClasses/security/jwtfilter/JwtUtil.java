package springStripeEcommerceService.mainClasses.security.jwtfilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {
	private static final SecretKey key = Jwts.SIG.HS256.key().build();
	private static final long EXP_TIME = 1800000; // Token expiry time in ms

	public static String generateToken(String username){
		return Jwts.builder()
				.subject(username)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() +    EXP_TIME))
				.signWith(key)
				.compact();
	}

	public static String extractUsername(String token){
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}

	public static boolean validateToken(String token){
		try {
			extractUsername(token);
			return true;
		} catch (Exception e) {
			System.out.println("Couldn't parse token33: " + e.getMessage());
		}
		return false;
	}
}
