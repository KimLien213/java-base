package com.base.infra.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class RSAKeyLoader {

    private final JwtConfig jwtConfig;

    @EventListener(ApplicationReadyEvent.class)
    public void loadRSAKeys() {
        try {
            RSAPrivateKey privateKey = loadPrivateKey();
            RSAPublicKey publicKey = loadPublicKey();

            jwtConfig.setRsaPrivateKey(privateKey);
            jwtConfig.setRsaPublicKey(publicKey);

            log.info("RSA Keys loaded successfully");
        } catch (Exception e) {
            log.error("Failed to load RSA keys", e);
            throw new RuntimeException("Failed to load RSA keys", e);
        }
    }

    private RSAPrivateKey loadPrivateKey() throws Exception {
        String privateKeyContent = getPrivateKeyContent();
        return parsePrivateKey(privateKeyContent);
    }

    private RSAPublicKey loadPublicKey() throws Exception {
        String publicKeyContent = getPublicKeyContent();
        return parsePublicKey(publicKeyContent);
    }

    private String getPrivateKeyContent() throws IOException {
        // Try PEM string first
        if (StringUtils.hasText(jwtConfig.getRsa().getPrivateKeyPem())) {
            return jwtConfig.getRsa().getPrivateKeyPem();
        }

        // Try resource file
        Resource privateKeyResource = jwtConfig.getRsa().getPrivateKey();
        if (privateKeyResource != null && privateKeyResource.exists()) {
            return privateKeyResource.getContentAsString(StandardCharsets.UTF_8);
        }

        throw new IllegalStateException("No private key configured. Set either jwt.rsa.private-key-pem or jwt.rsa.private-key");
    }

    private String getPublicKeyContent() throws IOException {
        // Try PEM string first
        if (StringUtils.hasText(jwtConfig.getRsa().getPublicKeyPem())) {
            return jwtConfig.getRsa().getPublicKeyPem();
        }

        // Try resource file
        Resource publicKeyResource = jwtConfig.getRsa().getPublicKey();
        if (publicKeyResource != null && publicKeyResource.exists()) {
            return publicKeyResource.getContentAsString(StandardCharsets.UTF_8);
        }

        throw new IllegalStateException("No public key configured. Set either jwt.rsa.public-key-pem or jwt.rsa.public-key");
    }

    private RSAPrivateKey parsePrivateKey(String privateKeyPem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyContent = privateKeyPem
                .replaceAll("\\n", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    private RSAPublicKey parsePublicKey(String publicKeyPem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyContent = publicKeyPem
                .replaceAll("\\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                .replace("-----END RSA PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(publicKeyContent);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }
}
