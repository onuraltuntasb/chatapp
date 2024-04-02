package com.chatapp.chatapp.security;

import com.chatapp.chatapp.exception.TokenCustomException;
import com.chatapp.chatapp.setup.AuthProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@NoArgsConstructor
@Component
public class JwtUtils {

    AuthProperties authProperties = new AuthProperties();

    private final String jwtSecret = authProperties.getJwtSecret();
    private final String jwtExpirationMs = authProperties.getJwtExpirationMs();


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean hasClaim(String token, String claimName) {
        final Claims claims = extractAllClaims(token); return claims.get(claimName) != null;
    }

    public String getString(String name, Claims claims) {
        Object v = claims.get(name); return v != null ? String.valueOf(v) : null;
    }

    public String getAuthorityClaim(String token) {
        Claims claims = extractAllClaims(token.substring(7)); String authorities = getString("authorities", claims);
        String st = authorities.substring(12);

        return st.substring(0, st.length() - 2);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {

        Claims claims = null;

        try {
            claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenCustomException(token, "Jwt token is expired");
        } catch (MalformedJwtException e) {
            throw new TokenCustomException(token, "Jwt token is malformed");
        } catch (SignatureException e) {
            throw new TokenCustomException(token, "Jwt token signature exception");
        } catch (Exception e) {
            throw new TokenCustomException(token, e.getMessage());
        }

        return claims;
    }


    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); return createToken(claims, userDetails);
    }

    public String createToken(Map<String, Object> claims, UserDetails userDetails) {

        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())

                //TODO authorities
                .claim("authorities", userDetails.getAuthorities())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Integer.parseInt(jwtExpirationMs)))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token); return (username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token));
    }

    public Boolean isTokenValid(String token, String email) {
        final String username = extractUsername(token); return (username.equals(email) && !isTokenExpired(token));
    }

    public String decodeJWTToken(String token) {
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String[] chunks = token.split("\\.");

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        return header + " " + payload;
    }



    public Boolean validateSecretAndExpiration(String token) throws Exception {

        String[] chunks = token.split("\\.");

        String tokenWithoutSignature = chunks[0] + "." + chunks[1];
        String signature = chunks[2];

        SignatureAlgorithm sa = HS256;
        SecretKeySpec secretKeySpec = new SecretKeySpec( jwtSecret.getBytes(), sa.getJcaName());

        DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(sa, secretKeySpec);

        if (!validator.isValid(tokenWithoutSignature, signature) || isTokenExpired(token)) {
            return false;
        }else{
            return true;
        }

    }


}