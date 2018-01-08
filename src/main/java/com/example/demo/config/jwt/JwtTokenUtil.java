package com.example.demo.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Create by ky.bai on 2018-01-08 8:49
 */
@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = 4709360429245714400L;

    private static final String AUDIENCE_UNKNOWN = "unknown";
    private static final String AUDIENCE_WEB = "web";
    private static final String AUDIENCE_MOBILE = "mobile";
    private static final String AUDIENCE_TABLET = "tablet";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Created by ky.bai on 2018-01-08 10:02
     *
     * @param token token
     *
     * @return username
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Created by ky.bai on 2018-01-08 10:02
     *
     * @param token token
     *
     * @return expiration date
     */
    private Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Created by ky.bai on 2018-01-08 10:03
     *
     * @param token token
     *
     * @return created date
     */
    private Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    /**
     * Created by ky.bai on 2018-01-08 10:03
     *
     * @param token token
     *
     * @return audience
     */
    private String getAudienceFromToken(String token) {
        return getClaimFromToken(token, Claims::getAudience);
    }

    /**
     * Created by ky.bai on 2018-01-08 10:03
     *
     * @param token          token
     * @param claimsResolver claims resolver type
     *
     * @return type value from claims
     */
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Created by ky.bai on 2018-01-08 10:05
     *
     * @param token       token
     * @param userDetails userDetails
     *
     * @return token is valid
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token) && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()));
    }

    /**
     * Created by ky.bai on 2018-01-08 10:05
     *
     * @return token
     */
    public String generateToken(UserDetails userDetails, Device device) {
        return doGenerationToken(new HashMap<>(), userDetails.getUsername(), generateAudience(device));
    }

    /**
     * Created by ky.bai on 2018-01-08 16:53
     *
     * @param token             token
     * @param lastPasswordReset last password reset
     *
     * @return token can be refreshed
     */
    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getIssuedAtDateFromToken(token);
        return (!isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && !isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    /**
     * Created by ky.bai on 2018-01-08 10:06
     *
     * @param token old token
     *
     * @return new token
     */
    public String refreshToken(String token) {
        final Date createDate = new Date();
        final Date expirationDate = calculateExpirationDate(createDate);

        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * Created by ky.bai on 2018-01-08 16:54
     *
     * @param token token
     *
     * @return get all claims
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * Created by ky.bai on 2018-01-08 10:06
     *
     * @param claims   hashMap
     * @param subject  username
     * @param audience request device
     *
     * @return token
     */
    private String doGenerationToken(Map<String, Object> claims, String subject, String audience) {
        final Date createDate = new Date();
        final Date expirationDate = calculateExpirationDate(createDate);

        return Jwts.builder().setClaims(claims).setSubject(subject).setAudience(audience).setIssuedAt(createDate).setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * Created by ky.bai on 2018-01-08 10:07
     *
     * @param token token
     *
     * @return token is valid
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Created by ky.bai on 2018-01-08 11:10
     *
     * @param token token
     *
     * @return ignore token expiration
     */
    private Boolean ignoreTokenExpiration(String token) {
        String audience = getAudienceFromToken(token);
        return (AUDIENCE_MOBILE.equals(audience) || AUDIENCE_TABLET.equals(token));
    }

    /**
     * Created by ky.bai on 2018-01-08 16:49
     *
     * @param created           created date
     * @param lastPasswordReset last password reset
     *
     * @return is created before last password reset
     */
    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    /**
     * Created by ky.bai on 2018-01-08 10:07
     *
     * @param createDate created date
     *
     * @return expiration date
     */
    private Date calculateExpirationDate(Date createDate) {
        return new Date(createDate.getTime() + expiration * 1000);
    }

    private String generateAudience(Device device) {
        String audience = AUDIENCE_UNKNOWN;
        if (device.isNormal()) {
            audience = AUDIENCE_WEB;
        } else if (device.isMobile()) {
            audience = AUDIENCE_MOBILE;
        } else if (device.isTablet()) {
            audience = AUDIENCE_TABLET;
        }
        return audience;
    }
}
