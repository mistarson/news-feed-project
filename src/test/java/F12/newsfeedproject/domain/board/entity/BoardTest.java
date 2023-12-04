package F12.newsfeedproject.domain.board.entity;

import static F12.newsfeedproject.testhelper.EntityCreator.createBoard;
import static F12.newsfeedproject.testhelper.EntityCreator.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;

import F12.newsfeedproject.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardTest {

    @Test
    @DisplayName("게시글을 수정할 수 있다.")
    void updateBoard(){
        //given
        User user = createUser("손창현", "cson90563@gmail.com");
        Board board = createBoard(1L, user);
        Board modifyBoard = Board.builder()
                .boardTitle("바꿀 제목")
                .boardContent("바꿀 내용")
                .build();

        // when
        board.update(modifyBoard);

        // then
        assertEquals(modifyBoard.getBoardTitle(), board.getBoardTitle());
        assertEquals(modifyBoard.getBoardContent(), board.getBoardContent());
    }
}