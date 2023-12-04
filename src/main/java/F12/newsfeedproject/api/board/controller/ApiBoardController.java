package F12.newsfeedproject.api.board.controller;

import F12.newsfeedproject.api.board.dto.request.BoardRequestDto;
import F12.newsfeedproject.api.board.dto.request.BoardUpdateRequestDto;
import F12.newsfeedproject.api.board.dto.response.BoardResponseDto;
import F12.newsfeedproject.api.board.dto.response.BoardViewResponseDto;
import F12.newsfeedproject.api.board.service.ApiBoardService;
import F12.newsfeedproject.domain.user.entity.User;
import F12.newsfeedproject.global.exception.member.UnAuthorizedModifyException;
import F12.newsfeedproject.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class ApiBoardController {

  private final ApiBoardService apiBoardService;

  // 게시글 작성
  @PostMapping
  public ResponseEntity<BoardResponseDto> saveBoard(
      @Valid @RequestBody BoardRequestDto requestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    User loginUser = userDetails.getUser();

    BoardResponseDto responseDto = apiBoardService.saveBoard(requestDto, loginUser);

    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
  }

  // 게시글 단건 조회
  @GetMapping("/{boardId}")
  public ResponseEntity<BoardResponseDto> getBoard(@PathVariable Long boardId) {
    BoardResponseDto responseDto = apiBoardService.getBoard(boardId);
    return ResponseEntity.ok(responseDto);
  }

  // 게시글 전체 목록 조회
  @GetMapping
  public ResponseEntity<List<BoardResponseDto>> getBoards() {
    List<BoardResponseDto> responseDto = apiBoardService.getBoards();
    return ResponseEntity.ok(responseDto);
  }

  // 게시글 수정
  @PatchMapping("/{boardId}")
  public ResponseEntity<BoardResponseDto> updateBoard(
      @PathVariable Long boardId,
      @Valid @RequestBody BoardUpdateRequestDto requestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    User user = userDetails.getUser();
    if (!haveModifyAuthorization(user, boardId)) {
      throw new UnAuthorizedModifyException();
    }

    BoardResponseDto responseDto = apiBoardService.updateBoard(boardId, requestDto);
    return ResponseEntity.ok(responseDto);
  }

  // 게시글 삭제
  @DeleteMapping("/{boardId}")
  public ResponseEntity<?> deleteBoard(@PathVariable Long boardId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    User user = userDetails.getUser();
    if (!haveModifyAuthorization(user, boardId)) {
      throw new UnAuthorizedModifyException();
    }

    apiBoardService.deleteBoard(boardId);
    return ResponseEntity.ok("요청 성공");
  }

  public boolean haveModifyAuthorization(User loginUser, Long boardId) {
    Long authorId = apiBoardService.getAuthorIdByBoardId(boardId);
    return loginUser.getUserId().equals(authorId);
  }

  @GetMapping("/follow-true")
  public ResponseEntity<List<BoardViewResponseDto>> getFollowersBoards(
      @AuthenticationPrincipal UserDetailsImpl userDetails,
      @PageableDefault(sort = "createdDate") Pageable pageable
  ) {
    User user = userDetails.getUser();
    List<BoardViewResponseDto> BoardViewResponseDto = apiBoardService.getFollowersBoards(
        user.getUserId(), pageable);

    return ResponseEntity.ok(BoardViewResponseDto);
  }

  @GetMapping("/like-true")
  public ResponseEntity<List<BoardViewResponseDto>> getLikeBoards(
      @AuthenticationPrincipal UserDetailsImpl userDetails,
      @PageableDefault(sort = "createdDate") Pageable pageable) {
    User user = userDetails.getUser();
    List<BoardViewResponseDto> BoardViewResponseDto = apiBoardService.getLikeBoards(
        user.getUserId(), pageable);

    return ResponseEntity.ok(BoardViewResponseDto);
  }
}

