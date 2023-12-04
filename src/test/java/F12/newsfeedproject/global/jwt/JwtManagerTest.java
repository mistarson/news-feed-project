package F12.newsfeedproject.global.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtManagerTest {

    JwtManager jwtManager;


    @Test
    @DisplayName("액세스 토큰을 발급할 수 있다.")
    void createAccessToken() {
        //given
        String accessToken = jwtManager.createAccessToken("손창현");
        System.out.println(accessToken);

        // when

        // then

    }

}