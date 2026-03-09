package com.example.iam.client;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakClient {

    @Value("${keycloak.admin-client-id}")
    private String adminClientId;

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.username}")
    private String username;

    @Value("${keycloak.password}")
    private String password;

    @Value("${keycloak.grant-type}")
    private String type;

    /**
     * Keycloak Admin Client 인스턴스를 생성하여 Spring Bean으로 등록합니다.
     *
     * @return 설정된 인증 정보로 초기화된 {@link Keycloak} 인스턴스
     */
    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .clientId(adminClientId)
                .grantType(type)
                .username(username)
                .password(password)
                .build();
    }
}