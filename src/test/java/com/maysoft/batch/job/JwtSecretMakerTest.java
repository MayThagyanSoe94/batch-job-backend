package com.maysoft.batch.job;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class JwtSecretMakerTest {

        @Test
        public void generateSecretKey() {
            SecretKey key = Jwts.SIG.HS512.key().build();
            String encodedKey = DatatypeConverter.printHexBinary(key.getEncoded());
            System.out.printf("\nKey = [%s]\n", encodedKey);
        }
}
