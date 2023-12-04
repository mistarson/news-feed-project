package F12.newsfeedproject.api.user.controller;

import static F12.newsfeedproject.testhelper.EntityCreator.createUser;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import F12.newsfeedproject.api.user.dto.request.UserModifyRequestDTO;
import F12.newsfeedproject.api.user.dto.request.UserSignupRequestDTO;
import F12.newsfeedproject.api.user.service.ApiUserService;
import F12.newsfeedproject.domain.user.entity.User;
import F12.newsfeedproject.filter.MockSpringSecurityFilter;
import F12.newsfeedproject.global.config.SecurityConfig;
import F12.newsfeedproject.global.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(
        controllers = ApiUserController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
@MockBean(JpaMetamodelMappingContext.class)
class ApiUserControllerTest {

    MockMvc mvc;

    Principal mockPrincipal;

    @Autowired
    WebApplicationContext context;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ApiUserService apiUserService;

    @BeforeEach
    public void setup() {

        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .alwaysDo(print())
                .build();

        mockUserSetup();
    }

    void mockUserSetup() {
        User user = createUser("손창현", "cson90563@gmail.com");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        mockPrincipal = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Nested
    @DisplayName("회원 가입 테스트")
    class SignupUser {

        @Test
        @DisplayName("회원 가입을 할 수 있다.")
        void signupUser() throws Exception {
            //given
            String userName = "손창현";
            String userPassword = "12345678";
            String userEmail = "cson90563@gmail.com";
            String userImageUrl = "s3-image-url.com";
            String userIntroduce = "안녕하세요 반갑습니다.";
            UserSignupRequestDTO userSignupRequestDTO = new UserSignupRequestDTO(
                    userName
                    , userPassword
                    , userEmail
                    , userImageUrl
                    , userIntroduce);
            String dtoToJson = objectMapper.writeValueAsString(userSignupRequestDTO);

            // when - then
            mvc.perform(post("/api/users/signup")
                            .content(dtoToJson)
                            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(mockPrincipal)
                    )
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("회원가입 정보 중 하나라도 내용이 빠져있다면 예외가 발생한다.")
        void signupUserByBlankInfo() throws Exception {
            //given
            String userPassword = "12345678";
            String userEmail = "cson90563@gmail.com";
            String userImageUrl = "s3-image-url.com";
            String userIntroduce = "안녕하세요 반갑습니다.";
            UserSignupRequestDTO userSignupRequestDTO = new UserSignupRequestDTO(
                    ""
                    , userPassword
                    , userEmail
                    , userImageUrl
                    , userIntroduce);
            String dtoToJson = objectMapper.writeValueAsString(userSignupRequestDTO);

            // when - then
            mvc.perform(post("/api/users/signup")
                            .content(dtoToJson)
                            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(mockPrincipal)
                    )
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("회원 수정 테스트")
    class UpdateUser {

        @Test
        @DisplayName("요청한 회원이 해당 회원의 수정 권한을 가지고 있다면 회원 정보를 수정할 수 있다.")
        void updateUserByHavingAuthorization() throws Exception {
            //given
            UserModifyRequestDTO userModifyRequestDTO = new UserModifyRequestDTO("google-image-url.com",
                    "안녕하세요. 바뀐 자기소개입니다.");
            String dtoToJson = objectMapper.writeValueAsString(userModifyRequestDTO);
            Long userId = 1L;

            // when - then
            mvc.perform(patch("/api/users/{userId}", userId)
                            .content(dtoToJson)
                            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(mockPrincipal)
                    )
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("요청한 회원이 해당 회원의 수정 권한을 가지고 있지 않다면 회원 정보를 수정할 수 없다.")
        void updateUserByNotHavingAuthorization() throws Exception {
            //given
            UserModifyRequestDTO userModifyRequestDTO = new UserModifyRequestDTO("google-image-url.com",
                    "안녕하세요. 바뀐 자기소개입니다.");
            String dtoToJson = objectMapper.writeValueAsString(userModifyRequestDTO);
            Long userId = 2L;

            // when - then
            mvc.perform(patch("/api/users/{userId}", userId)
                            .content(dtoToJson)
                            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(mockPrincipal)
                    )
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("리프레쉬 토큰을 통한 액세스 토큰 재발급 테스트")
    class ReissueAccessToken {
        @Test
        @DisplayName("리프레쉬 토큰을 통해 액세스 토큰을 재발급받을 수 있다.")
        void reissueAccessToken() throws Exception {
            //given
            String refreshToken = "Bearer refreshToken";

            // when - then
            mvc.perform(get("/api/users/reissue")
                            .header("Authorization", refreshToken)
                    )
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("회원 로그아웃 테스트")
    class LogoutUser {
        @Test
        @DisplayName("요청한 사용자를 로그아웃 시킬 수 있다.")
        void logoutUser() throws Exception {
            //given - when - then
            mvc.perform(get("/api/users/logout")
                            .principal(mockPrincipal))
                    .andExpect(status().isOk());
        }
    }
}