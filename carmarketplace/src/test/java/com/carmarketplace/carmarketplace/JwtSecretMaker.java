package com.carmarketplace.carmarketplace;

import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;

import javax.crypto.SecretKey;

public class JwtSecretMaker {

    @Test
    public void generateSecretKey() {
        SecretKey key = Jwts.SIG.HS512.key().build();
        String encodedkey = DatatypeConverter.printHexBinary(key.getEncoded());
        System.out.println(encodedkey);
    }
}
