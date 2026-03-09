package com.example.iam.service;

import com.example.iam.dto.UserRegistrationRecord;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Slf4j
public class KeycloakUserServiceImpl implements KeycloakUserService {

    @Value("${keycloak.realm}")
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
     * @throws UserCreationException 사용자 생성에 실패한 경우 (HTTP 201이 아닌 응답)
     * @throws IllegalArgumentException 필수 입력값이 누락된 경우
     */
    @Override
    public UserRegistrationRecord createUser(UserRegistrationRecord userRegistrationRecord) {
        validateUserRegistration(userRegistrationRecord);

        UserRepresentation user = buildUserRepresentation(userRegistrationRecord);

        UsersResource usersResource = getUsersResource();

        try (Response response = usersResource.create(user)) {
            if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                return userRegistrationRecord;
            }

            log.error("Keycloak 사용자 생성 실패 - status: {}, reason: {}",
                    response.getStatus(), response.getStatusInfo().getReasonPhrase());
            throw new UserCreationException(
                    "Keycloak 사용자 생성에 실패했습니다. HTTP Status: " + response.getStatus());
        }
    }

    private void validateUserRegistration(UserRegistrationRecord record) {
        Assert.notNull(record, "사용자 등록 정보는 null일 수 없습니다.");
        Assert.hasText(record.username(), "username은 필수 입력값입니다.");
        Assert.hasText(record.email(), "email은 필수 입력값입니다.");
        Assert.hasText(record.password(), "password는 필수 입력값입니다.");
    }

    private UserRepresentation buildUserRepresentation(UserRegistrationRecord record) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(record.username());
        user.setEmail(record.email());
        user.setFirstName(record.firstName());
        user.setLastName(record.lastName());
        user.setEmailVerified(true);

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setValue(record.password());
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);

        user.setCredentials(List.of(cred));
        return user;
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
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
        UsersResource usersResource = getUsersResource();
        UserRepresentation user = usersResource.get(userId).toRepresentation();
        usersResource.delete(userId);
        return user;
    }

    /**
     * Keycloak 사용자 생성 실패 시 발생하는 예외입니다.
     */
    public static class UserCreationException extends RuntimeException {
        public UserCreationException(String message) {
            super(message);
        }
    }
}