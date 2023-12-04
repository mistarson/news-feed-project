package F12.newsfeedproject.api.board.service;

import static F12.newsfeedproject.testhelper.EntityCreator.createBoard;
import static F12.newsfeedproject.testhelper.EntityCreator.createBoards;
import static F12.newsfeedproject.testhelper.EntityCreator.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import F12.newsfeedproject.api.board.dto.request.BoardRequestDto;
import F12.newsfeedproject.api.board.dto.request.BoardUpdateRequestDto;
import F12.newsfeedproject.api.board.dto.response.BoardResponseDto;
import F12.newsfeedproject.api.board.dto.response.BoardViewResponseDto;
import F12.newsfeedproject.domain.board.entity.Board;
import F12.newsfeedproject.domain.board.service.BoardService;
import F12.newsfeedproject.domain.user.entity.User;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ApiBoardServiceTest {

    @InjectMocks
    ApiBoardService apiBoardService;

    @Mock
    BoardService boardService;

    @BeforeAll
    static void setUp() {
        User user = createUser(100L, "손창현", "cson90563@gmail.com");
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }

    @Nested
    @DisplayName("게시글 저장 테스트")
    class SaveBoard {
        @Test
        @DisplayName("게시글을 저장할 수 있다.")
        void saveBoard() {
            //given
            User loginUser = getLoginUser();
            BoardRequestDto boardRequestDto = new BoardRequestDto("기존 제목", "기존 내용");

            Board board = createBoard(1L, loginUser);
            given(boardService.saveBoard(any(Board.class))).willReturn(board);

            // when
            BoardResponseDto saveBoardResponseDto = apiBoardService.saveBoard(boardRequestDto, loginUser);

            // then
            assertEquals(boardRequestDto.boardTitle(), saveBoardResponseDto.boardTitle());
            assertEquals(boardRequestDto.boardContent(), saveBoardResponseDto.boardContent());
        }
    }

    @Nested
    @DisplayName("게시글 조회 테스트")
    class GetBoard {
        @Test
        @DisplayName("게시글 Id에 따른 게시글을 조회할 수 있다.")
        void getBoard() {
            //given
            User loginUser = getLoginUser();

            Long boardId = 1L;
            Board board = createBoard(boardId, loginUser);

            given(boardService.findByBoardIdWithUser(boardId)).willReturn(board);

            // when
            BoardResponseDto boardResponseDto = apiBoardService.getBoard(boardId);

            // then
            assertEquals(board.getBoardId(), boardResponseDto.boardId());
            assertEquals(board.getBoardTitle(), boardResponseDto.boardTitle());
            assertEquals(board.getBoardContent(), boardResponseDto.boardContent());
            assertEquals(board.getUser().getUserName(), boardResponseDto.userName());
        }

        @Test
        @DisplayName("게시글의 전체 목록을 조회할 수 있다.")
        void getBoards() {
            //given
            User loginUser = getLoginUser();
            List<Board> boards = createBoards(loginUser);
            given(boardService.getBoards()).willReturn(boards);

            // when
            List<BoardResponseDto> boardResponseDtos = apiBoardService.getBoards();

            // then
            assertEquals(boards.size(), boardResponseDtos.size());
        }
    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class UpdateBoard {

        @Test
        @DisplayName("게시글을 수정할 수 있다.")
        void updateBoard() {
            //given
            User loginUser = getLoginUser();
            Long boardId = 1L;
            Board board = createBoard(boardId, loginUser);

            BoardUpdateRequestDto boardUpdateRequestDto = new BoardUpdateRequestDto("바뀐 제목", "바뀐 내용");

            given(boardService.findByBoardIdWithUser(boardId)).willReturn(board);

            // when
            apiBoardService.updateBoard(boardId, boardUpdateRequestDto);

            // then
            verify(boardService).findByBoardIdWithUser(boardId);
            verify(boardService).updateBoard(eq(board), any(Board.class));
        }
    }

    @Nested
    @DisplayName("게시글 삭제 테스트")
    class DeleteBoard {
        @Test
        @DisplayName("게시글 Id를 사용하여 게시글을 삭제할 수 있다.")
        void deleteBoard() {
            //given
            Long boardId = 100L;

            // when
            apiBoardService.deleteBoard(boardId);

            // then
            verify(boardService).deleteBoard(boardId);
        }
    }

    @Nested
    @DisplayName("팔로우 게시글 조회 테스트")
    class FollowBoard {
        @Test
        @DisplayName("사용자가 팔로우한 사용자의 게시글을 조회할 수 있다.")
        void getFollowersBoards() {
            //given
            User loginUser = getLoginUser();
            List<Board> followBoards = createBoards(createUser(2L, "장동하", "ehdgk@gmail.com"));
            given(boardService.findAllUserFollowerBoard(eq(loginUser.getUserId()), any())).willReturn(followBoards);

            // when
            List<BoardViewResponseDto> followersBoards = apiBoardService.getFollowersBoards(loginUser.getUserId(),
                    PageRequest.of(0, 10));

            // then
            assertEquals(followBoards.size(), followersBoards.size());
            assertEquals(followBoards.get(0).getUser().getUserName(), followersBoards.get(0).userName());
        }
    }

    @Nested
    @DisplayName("좋아요 게시글 조회 테스트")
    class LikeBoard {
        @Test
        @DisplayName("사용자가 좋아요를 누른 게시글을 조회할 수 있다.")
        void getLikeBoard() {
            //given
            User loginUser = getLoginUser();
            List<Board> likeBoards = createBoards(loginUser);
            given(boardService.findAllLikeBoards(eq(loginUser.getUserId()), any())).willReturn(likeBoards);

            // when
            List<BoardViewResponseDto> likeBoardDtos = apiBoardService.getLikeBoards(loginUser.getUserId(),
                    PageRequest.of(0, 10));

            // then
            assertEquals(likeBoards.size(), likeBoardDtos.size());
            assertEquals(likeBoards.get(0).getUser().getUserName(), likeBoardDtos.get(0).userName());
        }
    }

    User getLoginUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return (User) authentication.getPrincipal();
    }
}