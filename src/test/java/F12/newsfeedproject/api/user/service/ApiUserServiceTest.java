package F12.newsfeedproject.api.user.service;

import static F12.newsfeedproject.testhelper.EntityCreator.createUser;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import F12.newsfeedproject.api.user.dto.request.UserModifyRequestDTO;
import F12.newsfeedproject.api.user.dto.request.UserSignupRequestDTO;
import F12.newsfeedproject.api.user.dto.response.UserResponseDTO;
import F12.newsfeedproject.domain.user.entity.User;
import F12.newsfeedproject.domain.user.service.UserService;
import F12.newsfeedproject.global.exception.jwt.NotMisMatchedRefreshTokenException;
import F12.newsfeedproject.global.exception.jwt.NotRefreshTokenException;
import F12.newsfeedproject.global.exception.member.AlreadyUserExistException;
import F12.newsfeedproject.global.jwt.JwtManager;
import F12.newsfeedproject.global.jwt.TokenType;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ApiUserServiceTest {

    @InjectMocks
    ApiUserService apiUserService;

    @Mock
    UserService userService;

    @Mock
    JwtManager jwtManager;

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeAll
    static void setUp() {
        User user = createUser(100L, "손창현", "cson90563@gmail.com", "refreshToken");
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }

    @Nested
    @DisplayName("회원 회원가입 테스트")
    class SignupUser {
        @Test
        @DisplayName("회원가입을 진행할 수 있다.")
        void signupUser() {
            //given
            String userName = "손창현";
            String userPassword = "123456789";
            String userEmail = "cson90563@gmail.com";
            String userImageUrl = "s3-image-url.com";
            String userIntroduce = "안녕하세요 손창현입니다.";
            UserSignupRequestDTO userSignupRequestDTO =
                    new UserSignupRequestDTO(userName, userPassword, userEmail, userImageUrl, userIntroduce);

            User user = userSignupRequestDTO.toEntity(passwordEncoder);
            given(userService.findByUserName(userSignupRequestDTO.userName())).willReturn(Optional.empty());
            given(userService.saveUser(any(User.class))).willReturn(user);
            // when
            UserResponseDTO userResponseDTO = apiUserService.signupUser(userSignupRequestDTO);

            // then
            assertEquals(userSignupRequestDTO.userName(), userResponseDTO.userName());
            assertEquals(userSignupRequestDTO.userEmail(), userResponseDTO.userEmail());
            assertEquals(userSignupRequestDTO.userImageUrl(), userResponseDTO.userImageUrl());
            assertEquals(userSignupRequestDTO.userIntroduce(), userResponseDTO.userIntroduce());
        }

        @Test
        @DisplayName("기존 회원 중 동일한 이름이 있다면 해당 이름으로 회원가입 할 수 없다.")
        void signupUserByDuplicateUserName() {
            //given
            String userName = "손창현";
            String userPassword = "123456789";
            String userEmail = "thsckdgus@gmail.com";
            String userImageUrl = "s3-image-url.com";
            String userIntroduce = "안녕하세요 손창현입니다.";
            UserSignupRequestDTO userSignupRequestDTO =
                    new UserSignupRequestDTO(userName, userPassword, userEmail, userImageUrl, userIntroduce);

            User user = createUser(1L, "손창현", "cson90563@gmail.com");
            given(userService.findByUserName(userSignupRequestDTO.userName())).willReturn(Optional.of(user));
            // when

            // then
            assertThatThrownBy(() -> apiUserService.signupUser(userSignupRequestDTO))
                    .isInstanceOf(AlreadyUserExistException.class);
        }

        @Test
        @DisplayName("기존 회원 중 동일한 이메일이 있다면 해당 이메일로 회원가입 할 수 없다.")
        void signupUserByDuplicateUserEmail() {
            //given
            String userName = "아무개";
            String userPassword = "123456789";
            String userEmail = "cson90563@gmail.com";
            String userImageUrl = "s3-image-url.com";
            String userIntroduce = "안녕하세요 아무개입니다.";
            UserSignupRequestDTO userSignupRequestDTO =
                    new UserSignupRequestDTO(userName, userPassword, userEmail, userImageUrl, userIntroduce);

            User user = createUser(1L, "손창현", "cson90563@gmail.com");
            given(userService.findByUserEmail(userSignupRequestDTO.userEmail())).willReturn(Optional.of(user));
            // when

            // then
            assertThatThrownBy(() -> apiUserService.signupUser(userSignupRequestDTO))
                    .isInstanceOf(AlreadyUserExistException.class);
        }

        @Nested
        @DisplayName("회원 수정 테스트")
        class UpdateUser {
            @Test
            @DisplayName("회원 수정을 할 수 있다.")
            void updateUser() {
                //given
                User user = createUser(1L, "손창현", "cson90563@gmail.com");

                UserModifyRequestDTO userModifyRequestDTO =
                        new UserModifyRequestDTO("google-image-url.com", "아자아자 화이팅");

                given(userService.findByUserId(user.getUserId())).willReturn(Optional.of(user));

                // when
                apiUserService.updateUser(userModifyRequestDTO, user.getUserId());

                // then
                verify(userService).findByUserId(user.getUserId());
                verify(userService).updateUser(any(User.class), any(User.class));
            }
        }

        @Nested
        @DisplayName("리프레쉬 토큰을 통한 액세스 토큰 재발급 테스트")
        class ReissueAccessToken {
            @Test
            @DisplayName("리프레쉬 토큰을 통하여 액세스 토큰을 재발급 받는다.")
            void reissueAccessToken() {
                //given
                User loginUser = getLoginUser();
                String refreshToken = loginUser.getRefreshToken();

                given(jwtManager.getUserNameFromToken(any())).willReturn(loginUser.getUserName());
                given(jwtManager.getTokenTypeFromToken(refreshToken)).willReturn(TokenType.REFRESH.toString());
                given(userService.findByUserName(loginUser.getUserName())).willReturn(Optional.of(loginUser));

                // when
                String accessToken = apiUserService.reissueAccessToken(refreshToken);

                // then
                String userNameFromRefreshToken = jwtManager.getUserNameFromToken(refreshToken);
                String userNameFromNewAccessToken = jwtManager.getUserNameFromToken(accessToken);

                assertEquals(userNameFromRefreshToken, userNameFromNewAccessToken);
            }

            @Test
            @DisplayName("리프레쉬 토큰이 아니면 예외가 발생한다.")
            void reissueTokenByNotRefreshToken() {
                //given
                User loginUser = getLoginUser();
                String notRefreshToken = "notRefreshToken";

                given(jwtManager.getUserNameFromToken(any())).willReturn(loginUser.getUserName());
                given(jwtManager.getTokenTypeFromToken(any())).willReturn(TokenType.ACCESS.toString());
                // when

                // then
                assertThatThrownBy(() -> apiUserService.reissueAccessToken(notRefreshToken))
                        .isInstanceOf(NotRefreshTokenException.class);

            }

            @Test
            @DisplayName("DB에서 조회해 온 리프레쉬 토큰과 다르다면 예외가 발생한다.")
            void reissueTokenByNotRightRefreshToken() {
                //given
                User loginUser = getLoginUser();
                String userName = loginUser.getUserName();
                String otherRefreshToken = "otherRefreshToken";

                given(jwtManager.getUserNameFromToken(otherRefreshToken)).willReturn(userName);
                given(jwtManager.getTokenTypeFromToken(any())).willReturn(TokenType.REFRESH.toString());
                given(userService.findByUserName(userName)).willReturn(Optional.of(loginUser));

                // when

                // then
                assertThatThrownBy(() -> apiUserService.reissueAccessToken(otherRefreshToken))
                        .isInstanceOf(NotMisMatchedRefreshTokenException.class);
            }
        }

        @Nested
        @DisplayName("회원 로그아웃 테스트")
        class LogoutUser {
            @Test
            @DisplayName("")        
            void logoutUser(){
                //given
                Long userId = 1L;
                
                // when
                apiUserService.logoutUser(userId);

                // then
                verify(userService).logoutUser(userId);
            }
        }
    }

    User getLoginUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return (User) authentication.getPrincipal();
    }

}