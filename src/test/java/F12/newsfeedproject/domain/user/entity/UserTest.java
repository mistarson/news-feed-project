package F12.newsfeedproject.domain.user.entity;

import static F12.newsfeedproject.testhelper.EntityCreator.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    @DisplayName("회원 정보를 수정할 수 있다.")
    void updateUser(){
        //given
        User user = createUser("손창현", "cson90563@gmail.com");
        User modifyUser = User.builder()
                .userImageUrl("바뀐 이미지 주소")
                .userIntroduce("바뀐 자기소개")
                .build();

        // when
        user.updateUser(modifyUser);

        // then
        assertEquals(user.getUserImageUrl(), modifyUser.getUserImageUrl());
        assertEquals(user.getUserIntroduce(), modifyUser.getUserIntroduce());
    }

}