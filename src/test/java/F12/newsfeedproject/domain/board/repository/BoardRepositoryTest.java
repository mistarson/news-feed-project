package F12.newsfeedproject.domain.board.repository;

import static F12.newsfeedproject.testhelper.EntityCreator.createBoards;
import static F12.newsfeedproject.testhelper.EntityCreator.createFollow;
import static F12.newsfeedproject.testhelper.EntityCreator.createUser;

import F12.newsfeedproject.domain.board.entity.Board;
import F12.newsfeedproject.domain.follow.entity.Follow;
import F12.newsfeedproject.domain.follow.repository.FollowRepository;
import F12.newsfeedproject.domain.user.entity.User;
import F12.newsfeedproject.domain.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

    @Test
    @DisplayName("최신순으로 게시글을 조회할 수 있습니다.")
    void findAllByOrderByCreatedDateDesc(){
        //given
        User user = createUser(1L, "손창현", "cson90563@gmail.com");
        userRepository.save(user);
        List<Board> createdBoards = createBoards(user);
        boardRepository.saveAll(createdBoards);

        // when
        List<Board> boards = boardRepository.findAllByOrderByCreatedDateDesc();

        // then
        for (int i = 0; i < boards.size() - 1; i++) {
            Board current = boards.get(i);
            Board before = boards.get(i + 1);
            Assertions.assertTrue(current.getCreatedDate().isAfter(before.getCreatedDate()));
        }
    }

    @Test
    @DisplayName("팔로우한 사용자가 작성한 게시글을 조회할 수 있다.")
    void findAllUserFollowerBoard(){
        //given
        User user = createUser(1L, "손창현", "cson90563@gmail.com");
        User followingUser = createUser(2L, "아무개", "아무개@gmail.com");
        userRepository.save(user);
        userRepository.save(followingUser);

        Follow follow = createFollow(followingUser, user);
        followRepository.save(follow);

        List<Board> boards = createBoards(followingUser);
        boardRepository.saveAll(boards);

        // when
//        Page<Board> boardByFollow = boardRepository.findAllUserFollowerBoard(user.getUserId(),
//                PageRequest.of(0, 10));

        // then


    }





}