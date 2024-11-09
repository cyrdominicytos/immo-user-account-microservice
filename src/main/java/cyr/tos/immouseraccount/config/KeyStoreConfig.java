package cyr.tos.immouseraccount.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Configuration
class KeyStoreConfig {
    @Value("${keyStore.password}")
    private String keyStorePassword;

    @Value("${keyStore.path}")
    private String keyStorePath;

    @Bean
    public KeyStore keyStore() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new ClassPathResource(keyStorePath).getInputStream(), keyStorePassword.toCharArray());
        return keyStore;

        /*KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        try(InputStream keyStoreStream = getClass().getClassLoader().getResourceAsStream(keyStorePath))
        {
            if(keyStoreStream == null)
                throw new RuntimeException("KeyStore file not found");
            keyStore.load(keyStoreStream, keyStorePassword.toCharArray());
        }
        return keyStore;*/
    }
}
