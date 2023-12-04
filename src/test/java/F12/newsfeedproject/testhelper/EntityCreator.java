package F12.newsfeedproject.testhelper;

import F12.newsfeedproject.domain.board.entity.Board;
import F12.newsfeedproject.domain.comment.entity.Comment;
import F12.newsfeedproject.domain.follow.entity.Follow;
import F12.newsfeedproject.domain.user.constant.UserRole;
import F12.newsfeedproject.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;

public class EntityCreator {

    public static User createUser(String userName, String userEmail) {
        return User.builder()
                .userId(1L)
                .userName(userName)
                .userPassword("1234")
                .userEmail(userEmail)
                .userImageUrl("s3-image-url.com")
                .userIntroduce("안녕하세요 + " + userName + "입니다.")
                .userRole(UserRole.USER)
                .build();
    }

    public static User createUser(Long userId, String userName, String userEmail) {
        return User.builder()
                .userId(userId)
                .userName(userName)
                .userPassword("1234")
                .userEmail(userEmail)
                .userImageUrl("s3-image-url.com")
                .userIntroduce("안녕하세요 + " + userName + "입니다.")
                .userRole(UserRole.USER)
                .build();
    }

    public static User createUser(Long userId, String userName, String userEmail, String refreshToken) {
        return User.builder()
                .userId(userId)
                .userName(userName)
                .userPassword("1234")
                .userEmail(userEmail)
                .userImageUrl("s3-image-url.com")
                .userIntroduce("안녕하세요 + " + userName + "입니다.")
                .userRole(UserRole.USER)
                .refreshToken(refreshToken)
                .build();
    }

    public static Board createBoard(Long boardId, User user) {
        return Board.builder()
                .boardId(100L)
                .boardTitle("기존 제목")
                .boardContent("기존 내용")
                .user(user)
                .build();
    }

    public static List<Board> createBoards(User user) {
        List<Board> boards = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            boards.add(createBoard(i+1L, user));
        }
        return boards;
    }

    public static Comment createComment(Long commentId, String commentContent, Board board, User user) {
        return Comment.builder()
                .commentId(commentId)
                .commentContent(commentContent)
                .board(board)
                .user(user)
                .build();
    }

    public static Follow createFollow(User followerUser, User followingUser) {
        return Follow.builder()
                .follower(followerUser)
                .following(followingUser)
                .build();
    }
}
