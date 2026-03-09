package com.example.iam.dto;

/**
 * 사용자 등록 요청 정보를 담는 DTO 레코드.
 *
 * @param username  사용자 로그인 아이디
 * @param email     사용자 이메일 주소
 * @param firstName 사용자 이름 (First Name)
 * @param lastName  사용자 성 (Last Name)
 * @param password  사용자 비밀번호
 */
public record UserRegistrationRecord(String username, String email, String firstName, String lastName, String password) {

}