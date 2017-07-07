package com.kovacskornel.tlog16rs.resources;

import io.jsonwebtoken.SignatureAlgorithm;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

/**
 *
 * @author Kovacs Kornel
 */
public class JwtService {
    private static final String secret = "sage4hASDGwahdsgfaA";

    public static String createJWT(String user) throws JoseException, UnsupportedEncodingException {
        
        JwtClaims claims = new JwtClaims();
        JsonWebSignature jws = new JsonWebSignature();
        Key key = new HmacKey(secret.getBytes("UTF-8"));
        String jwt;
        int expirationTimeInMinutes = 5;
        claims.setExpirationTimeMinutesInTheFuture(expirationTimeInMinutes);
        claims.setSubject(user);
        jws.setPayload(claims.toJson());
        jws.setKey(key);
        jws.setKeyIdHeaderValue("kid");
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setDoKeyValidation(false);
        jwt = jws.getCompactSerialization();
        return jwt;
    }

    /**
     *
     * @param token
     * @return
     * @throws InvalidJwtException
     */
    public static String getNameFromToken(String token) throws InvalidJwtException {
        JwtConsumer jwtConsumer;
        jwtConsumer = new JwtConsumerBuilder()
                .setVerificationKey(new HmacKey(secret.getBytes()))
                .setRelaxVerificationKeyValidation()
                .setSkipSignatureVerification()
                .build();
        jwtConsumer.processContext(jwtConsumer.process(token));
        JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
        String name = jwtClaims.getClaimValue("sub").toString();
        return name;
    }
}