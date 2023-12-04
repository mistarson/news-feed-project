package F12.newsfeedproject.global.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import F12.newsfeedproject.global.exception.jwt.InvalidJwtSignatureException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtManagerSpringBootTest {

    @Autowired
    JwtManager jwtManager;

    @Test
    @DisplayName("액세스 토큰을 발급할 수 있다.")
    void createAccessToken() {
        //given
        String userName = "손창현";

        // when
        String accessToken = jwtManager.createAccessToken("손창현");

        // then
        assertNotNull(accessToken);
    }

    @Test
    @DisplayName("토큰으로부터 사용자 이름을 얻을 수 있다.")
    void getUserNameFromToken() {
        //given
        String userName = "손창현";
        String accessToken = jwtManager.createAccessToken("손창현");

        // when
        String userNameFromToken = jwtManager.getUserNameFromToken(accessToken);

        // then
        assertEquals(userName, userNameFromToken);
    }

    @Test
    @DisplayName("토큰으로부터 토큰 타입을 얻을 수 있다.")
    void getTokenTypeFromToken() {
        //given
        String userName = "손창현";
        String accessToken = jwtManager.createAccessToken("손창현");

        // when
        String tokenTypeFromToken = jwtManager.getTokenTypeFromToken(accessToken);

        // then
        assertEquals(TokenType.ACCESS.toString(), tokenTypeFromToken);
    }

    @Test
    @DisplayName("")
    void validateTokenThrowInvalidJwtSignatureException() {
        //given
        String accessToken = "Bearer 1234";

        // when - then
        Assertions.assertThatThrownBy(() -> jwtManager.validateToken(accessToken))
                .isInstanceOf(InvalidJwtSignatureException.class);
    }
}
