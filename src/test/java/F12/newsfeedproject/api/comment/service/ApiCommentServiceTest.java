package F12.newsfeedproject.api.comment.service;

import static F12.newsfeedproject.testhelper.EntityCreator.createBoard;
import static F12.newsfeedproject.testhelper.EntityCreator.createComment;
import static F12.newsfeedproject.testhelper.EntityCreator.createUser;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import F12.newsfeedproject.api.comment.dto.CommentRequestDTO;
import F12.newsfeedproject.api.comment.dto.CommentResponseDTO;
import F12.newsfeedproject.domain.board.entity.Board;
import F12.newsfeedproject.domain.board.service.BoardService;
import F12.newsfeedproject.domain.comment.entity.Comment;
import F12.newsfeedproject.domain.comment.service.CommentService;
import F12.newsfeedproject.domain.user.entity.User;
import F12.newsfeedproject.global.exception.comment.RejectedExecutionException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ApiCommentServiceTest {

    @InjectMocks
    ApiCommentService apiCommentService;

    @Mock
    BoardService boardService;

    @Mock
    CommentService commentService;

    @BeforeAll
    static void setUp() {
        User user = createUser(100L, "손창현", "cson90563@gmail.com");
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null));
    }

    @Nested
    @DisplayName("댓글 저장 테스트")
    class SaveComment {
        @Test
        @DisplayName("댓글을 저장할 수 있다.")
        @PreAuthorize("isAuthenticated()")
        void saveComment() {
            //given
            User loginUser = getLoginUser();
            Board board = createBoard(100L, loginUser);
            CommentRequestDTO commentRequestDTO = new CommentRequestDTO(board.getBoardId(), "잘봤습니다.");
            Comment comment = Comment.createComment(commentRequestDTO.commentContent(), loginUser, board);

            given(boardService.findByBoardId(board.getBoardId())).willReturn(board);
            given(commentService.saveComment(any(Comment.class))).willReturn(comment);

            // when
            CommentResponseDTO commentResponseDTO = apiCommentService.createComment(commentRequestDTO, loginUser);

            // then
            assertEquals(commentRequestDTO.commentContent(), commentResponseDTO.getCommentContent());
        }
    }

    @Nested
    @DisplayName("댓글 수정 테스트")
    class UpdateComment {
        @Test
        @DisplayName("로그인한 사용자와 댓글 작성자가 같다면 댓글을 수정할 수 있다.")
        void updateCommentByAuthor() {
            //given
            User loginUser = getLoginUser();
            Board board = createBoard(1L, loginUser);
            Comment comment = createComment(1L, "기존 내용", board, loginUser);

            CommentRequestDTO commentRequestDTO = new CommentRequestDTO(board.getBoardId(), "바뀐 내용");

            given(commentService.findByCommentId(comment.getCommentId())).willReturn(Optional.of(comment));

            // when
            apiCommentService.updateComment(comment.getCommentId(), commentRequestDTO, loginUser);

            // then
            verify(commentService).findByCommentId(comment.getCommentId());
            verify(commentService).updateComment(comment, commentRequestDTO.commentContent());
        }

        @Test
        @DisplayName("로그인한 사용자와 댓글 작성자가 다르면 예외가 발생한다.")
        void updateCommentByNotAuthor() {
            //given
            User loginUser = getLoginUser();
            Board board = createBoard(1L, loginUser);
            User notAuthorUser = createUser(2L, "아무개", "dkanro@gmail.com");
            Comment comment = createComment(1L, "기존 내용", board, notAuthorUser);

            CommentRequestDTO commentRequestDTO = new CommentRequestDTO(board.getBoardId(), "바뀐 내용");

            given(commentService.findByCommentId(comment.getCommentId())).willReturn(Optional.of(comment));
            // when

            // then
            assertThatThrownBy(() -> apiCommentService
                    .updateComment(comment.getCommentId(), commentRequestDTO, loginUser))
                    .isInstanceOf(RejectedExecutionException.class);
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class DeleteComment {
        @Test
        @DisplayName("로그인한 사용자와 댓글 작성자가 같다면 댓글을 삭제할 수 있다.")
        void deleteCommentByAuthor() {
            //given
            User loginUser = getLoginUser();
            User notAuthorUser = createUser(2L, "아무개", "cson90563@gmail.com");
            Board board = createBoard(1L, loginUser);
            Comment comment = createComment(1L, "잘봤습니다.", board, loginUser);


            given(commentService.findByCommentId(comment.getCommentId())).willReturn(Optional.of(comment));

            // when
            apiCommentService.deleteComment(comment.getCommentId(), loginUser);

            // then
            verify(commentService).deleteComment(comment.getCommentId());
        }

        @Test
        @DisplayName("로그인한 사용자와 댓글 작성자가 다르면 예외가 발생한다.")
        void deleteCommentByNotAuthor() {
            //given
            User loginUser = getLoginUser();
            User notAuthorUser = createUser(2L, "아무개", "dkanro@gmail.com");
            Board board = createBoard(1L, loginUser);
            Comment comment = createComment(1L, "잘봤습니다.", board, notAuthorUser);

            given(commentService.findByCommentId(comment.getCommentId())).willReturn(Optional.of(comment));

            // when

            // then
            assertThatThrownBy(() -> apiCommentService.deleteComment(comment.getCommentId(), loginUser))
                    .isInstanceOf(RejectedExecutionException.class);
        }
    }

    User getLoginUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return (User) authentication.getPrincipal();
    }
}