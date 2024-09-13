package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private  User user;
    @InjectMocks
    private UserService userService;

    @Test
    public void user_목록_조회_시_userId가_없다면_InvalidRequestException_에러를_던진다(){
        long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.getUser(userId));

        assertEquals("User not found", exception.getMessage());

    }

    @Test
    public void 비밀번호의_규칙을_지키지_않으면_InvalidRequestException_에러를_던진다(){
        long userId = 1L;
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(passwordEncoder.encode("Wjh4302869!")
                ,"rlvuddl1234");

        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
        }

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(userId, userChangePasswordRequest));

        assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());

    }

    @Test
    public void 전_비밀번호와_이전_비밀번호가_같으면_InvalidRequestException_에러를_던진다(){
        long userId = 1L;
        String Beforepassword = "Wjh4302869!";
        String Afterpassword = "Wjh4302869!";

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(user.getPassword()).willReturn(Beforepassword);
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(Beforepassword,Afterpassword);

        when(passwordEncoder.matches(Afterpassword, Beforepassword)).thenReturn(true);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(userId, userChangePasswordRequest));

        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());

    }

    @Test
    public void 기존_비밀번호가_올바르지_않으면_InvalidRequestException_에러를_던진다(){
        long userId = 1L;
        String Beforepassword = "wjh4302869!";
        String Afterpassword = "Rlvudd1234!";

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(user.getPassword()).willReturn(Beforepassword);
        UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(Beforepassword,Afterpassword);

        when(passwordEncoder.matches(Afterpassword, Beforepassword)).thenReturn(false);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(userId, userChangePasswordRequest));

        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());

    }




}
