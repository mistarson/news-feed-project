package F12.newsfeedproject.domain.comment.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentTest {

    @Test
    @DisplayName("댓글을 수정할 수 있다.")
    void updateComment(){
        //given
        Comment comment = Comment.builder().commentContent("기존 댓글 내용").build();
        String updateCommentContent = "바꿀 댓글 내용";

        // when
        comment.updateCommentContent(updateCommentContent);

        // then
        assertEquals(updateCommentContent, comment.getCommentContent());
    }

}