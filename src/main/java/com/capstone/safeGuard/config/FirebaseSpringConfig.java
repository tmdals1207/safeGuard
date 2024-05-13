package com.capstone.safeGuard.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Log4j2
public class FirebaseSpringConfig {

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @Bean
    GoogleCredentials googleCredentials() {
        try {
            File file = ResourceUtils.getFile("classpath:auth_fcm.json");
            FileInputStream in = new FileInputStream(file);
            return GoogleCredentials.fromStream(in);
        } catch (IOException e) {
            return null;
        }
    }


    @Bean
    FirebaseApp firebaseApp(GoogleCredentials credentials) {
        if(FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

}
