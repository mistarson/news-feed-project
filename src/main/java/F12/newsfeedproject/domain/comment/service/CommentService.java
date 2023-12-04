package F12.newsfeedproject.domain.comment.service;

    import F12.newsfeedproject.domain.comment.entity.Comment;
    import F12.newsfeedproject.domain.comment.repository.CommentRepository;
    import java.util.Optional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Optional<Comment> findByCommentId(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public void updateComment(Comment comment, String commentContent) {
        comment.updateCommentContent(commentContent);
    }
}