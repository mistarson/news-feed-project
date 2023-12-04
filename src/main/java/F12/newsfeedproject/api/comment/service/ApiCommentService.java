package F12.newsfeedproject.api.comment.service;

import F12.newsfeedproject.api.comment.dto.CommentRequestDTO;
import F12.newsfeedproject.api.comment.dto.CommentResponseDTO;
import F12.newsfeedproject.domain.board.entity.Board;
import F12.newsfeedproject.domain.board.service.BoardService;
import F12.newsfeedproject.domain.comment.entity.Comment;
import F12.newsfeedproject.domain.comment.service.CommentService;
import F12.newsfeedproject.domain.user.entity.User;
import F12.newsfeedproject.global.exception.comment.NotFoundCommentException;
import F12.newsfeedproject.global.exception.comment.RejectedExecutionException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiCommentService {

    private final CommentService commentService;
    private final BoardService boardService;

    @Transactional
    public CommentResponseDTO createComment(CommentRequestDTO dto, User user) {
        Board board = boardService.findByBoardId(dto.boardId());
        Comment comment = Comment.createComment(dto.commentContent(), user, board);
        Comment saveComment = commentService.saveComment(comment);

        return new CommentResponseDTO(saveComment);
    }

    @Transactional
    public CommentResponseDTO updateComment(Long commentId, CommentRequestDTO commentRequestDTO,
                                            User user) {
        Comment comment = getUserComment(commentId);
        validateAuthorization(user, comment);
        commentService.updateComment(comment, commentRequestDTO.commentContent());

        return new CommentResponseDTO(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = getUserComment(commentId);
        validateAuthorization(user, comment);

        commentService.deleteComment(commentId);
    }

    private Comment getUserComment(Long commentId) {
        return commentService.findByCommentId(commentId)
                .orElseThrow(NotFoundCommentException::new);
    }

    private void validateAuthorization(User user, Comment comment) {
        if (!user.getUserId().equals(comment.getUser().getUserId())) {
            throw new RejectedExecutionException();
        }
    }

}