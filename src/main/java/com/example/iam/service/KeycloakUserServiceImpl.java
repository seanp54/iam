package com.example.iam.service;

import com.example.iam.dto.UserRegistrationRecord;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class KeycloakUserServiceImpl implements KeycloakUserService {

    @Value("master")
    private String realm;

    private final Keycloak keycloak;

    public KeycloakUserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    /**
     * Keycloak에 신규 사용자를 생성합니다.
     *
     * @param userRegistrationRecord 생성할 사용자의 등록 정보 (username, email, password 등)
     * @return 생성된 사용자의 등록 정보
     * @throws RuntimeException 사용자 생성에 실패한 경우 (HTTP 201이 아닌 응답)
     */
    @Override
    public UserRegistrationRecord createUser(UserRegistrationRecord userRegistrationRecord) {

        // Create User
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userRegistrationRecord.username());
        user.setEmail(userRegistrationRecord.email());
        user.setFirstName(userRegistrationRecord.firstName());
        user.setLastName(userRegistrationRecord.lastName());
        user.setEmailVerified(true);

        // Set Credential
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setValue(userRegistrationRecord.password());
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);

        user.setCredentials(List.of(cred));

        UsersResource usersResource = getUsersResource();

        Response response = usersResource.create(user);

        if (Objects.equals(201, response.getStatus())) {
            return userRegistrationRecord;
        }

        log.error("Keycloak 사용자 생성 실패 - status: {}, reason: {}",
                response.getStatus(), response.getStatusInfo().getReasonPhrase());
        throw new RuntimeException(
                "Keycloak 사용자 생성에 실패했습니다. HTTP Status: " + response.getStatus());
    }

    private UsersResource getUsersResource() {
        RealmResource realm1 = keycloak.realm(realm);
        return realm1.users();
    }

    /**
     * 사용자 ID로 Keycloak에서 사용자 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 Keycloak ID
     * @return 조회된 사용자의 표현 객체
     */
    @Override
    public UserRepresentation getUserById(String userId) {
        return getUsersResource().get(userId).toRepresentation();
    }

    /**
     * 사용자 ID로 Keycloak에서 사용자를 삭제합니다.
     *
     * @param userId 삭제할 사용자의 Keycloak ID
     * @return 삭제된 사용자의 표현 객체 (삭제 전 조회된 정보)
     */
    @Override
    public UserRepresentation deleteUserById(String userId) {
        UserRepresentation user = getUsersResource().get(userId).toRepresentation();
        getUsersResource().delete(userId);
        return user;
    }
}