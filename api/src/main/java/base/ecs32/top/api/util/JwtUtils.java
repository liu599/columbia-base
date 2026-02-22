package base.ecs32.top.api.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtUtils {

    private static final String SECRET_KEY = "your-very-secure-and-long-secret-key-that-must-be-at-least-32-chars";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours in milliseconds

    public static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return createToken(claims, username);
    }

    private static String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static String getValidationError(String token) {
        try {
            Date expiration = extractExpiration(token);
            boolean expired = isTokenExpired(token);
            if (expired) {
                return "Token 已过期 (过期时间: " + expiration + ")";
            }
            return null; // 无错误
        } catch (io.jsonwebtoken.security.SignatureException e) {
            return "签名无效";
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            return "Token 格式错误";
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return "Token 已过期 (过期时间: " + e.getClaims().getExpiration() + ")";
        } catch (Exception e) {
            return "解析失败: " + e.getClass().getSimpleName();
        }
    }

    public static Boolean validateToken(String token) {
        return getValidationError(token) == null;
    }
}
