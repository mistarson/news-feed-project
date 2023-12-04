package F12.newsfeedproject.domain.user.repository;

import static F12.newsfeedproject.testhelper.EntityCreator.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import F12.newsfeedproject.domain.user.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("해당 이름을 가진 회원을 조회할 수 있다.")
    void findByUserName(){
        //given
        User user = createUser("손창현", "cson90563@gmail.com");
        userRepository.save(user);

        // when
        Optional<User> optionalFindUser = userRepository.findByUserName(user.getUserName());

        // then
        assertTrue(optionalFindUser.isPresent());

        User findUser = optionalFindUser.get();
        assertEquals(user.getUserId(), findUser.getUserId());
        assertEquals(user.getUserName(), findUser.getUserName());
        assertEquals(user.getUserEmail(), findUser.getUserEmail());
    }
    
    @Test
    @DisplayName("해당 이메일을 가진 회원을 조회할 수 있다.")
    void findByUserEmail(){
        //given
        String email = "cson90563@gmail.com";
        User user = createUser("손창현", email);
        userRepository.save(user);
        
        // when
        Optional<User> optionalFindUser = userRepository.findByUserEmail(email);

        // then
        assertTrue(optionalFindUser.isPresent());

        User findUser = optionalFindUser.get();
        assertEquals(user.getUserId(), findUser.getUserId());
        assertEquals(user.getUserName(), findUser.getUserName());
        assertEquals(user.getUserEmail(), findUser.getUserEmail());
    }

}