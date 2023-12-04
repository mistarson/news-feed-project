package F12.newsfeedproject.api.board.controller;

import static F12.newsfeedproject.testhelper.EntityCreator.createUser;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import F12.newsfeedproject.api.board.dto.request.BoardRequestDto;
import F12.newsfeedproject.api.board.service.ApiBoardService;
import F12.newsfeedproject.domain.user.entity.User;
import F12.newsfeedproject.filter.MockSpringSecurityFilter;
import F12.newsfeedproject.global.config.SecurityConfig;
import F12.newsfeedproject.global.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(
        controllers = ApiBoardController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
@MockBean(JpaMetamodelMappingContext.class)
class ApiBoardControllerTest {

    MockMvc mvc;

    Principal mockPrincipal;

    @Autowired
    WebApplicationContext context;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ApiBoardService apiBoardService;

    @BeforeEach
    public void setup() {

        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .alwaysDo(print())
                .build();

        mockUserSetup();
    }

    void mockUserSetup() {
        User user = createUser("손창현", "cson90563@gmail.com");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        mockPrincipal = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    
    @Nested
    @DisplayName("게시글을 저장 테스트")
    class SaveBoard {
        @Test
        @DisplayName("게시글을 저장할 수 있다.")        
        void saveBoard() throws Exception {
            //given
            BoardRequestDto boardRequestDto = new BoardRequestDto("기존 제목", "기존 내용");
            String dtoToJson = objectMapper.writeValueAsString(boardRequestDto);

            // when - then
            mvc.perform(post("/api/boards")
                            .content(dtoToJson)
                            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(mockPrincipal)
                    )
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("게시글 단건 조회 테스트")
    class GetBoard {
        @Test
        @DisplayName("게시글 Id를 통하여 게시글을 조회할 수 있다.")
        void getBoard() throws Exception {
            //given
            Long boardId = 1L;

            // when - then
            mvc.perform(get("/api/boards/{boardId}", boardId)
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(mockPrincipal)
                    )
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("게시물 전체 목록 조회 테스트")
    class GetBoards {
        @Test
        @DisplayName("게시물 전체 목록을 조회할 수 있다.")
        void getBoards() throws Exception {
            // when - then
            mvc.perform(get("/api/boards")
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(mockPrincipal)
                    )
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class UpdateBoard {

        @Test
        @DisplayName("요청한 회원과 게시글을 작성한 회원이 같다면 게시글을 수정할 수 있다.")
        void updateBoardByAuthor() throws Exception {
            //given
            BoardRequestDto boardRequestDto = new BoardRequestDto("바꿀 제목", "바꿀 내용");
            String dtoToJson = objectMapper.writeValueAsString(boardRequestDto);

            Long boardId = 1L;
            given(apiBoardService.getAuthorIdByBoardId(boardId))
                    .willReturn(1L);

            // when - then
            mvc.perform(patch("/api/boards/{boardId}", boardId)
                            .content(dtoToJson)
                            .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(mockPrincipal)
                    )
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("요청한 회원과 게시글을 작성한 회원이 다르면 게시글을 수정할 수 없다.")
        void updateBoardByNotAuthor() throws Exception {
            //given
            BoardRequestDto boardRequestDto = new BoardRequestDto("바꿀 제목", "바꿀 내용");
            String dtoToJson = objectMapper.writeValueAsString(boardRequestDto);

            Long otherUserId = 2L;

            Long boardId = 1L;
            given(apiBoardService.getAuthorIdByBoardId(boardId))
                    .willReturn(otherUserId);

            // when - then
            mvc.perform(patch("/api/boards/{boardId}", boardId)
                    .content(dtoToJson)
                    .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                    .accept(MediaType.APPLICATION_JSON)
                    .principal(mockPrincipal)
            )
                    .andExpect(status().isUnauthorized());
        }
    }
    
    @Nested
    @DisplayName("게시글 삭제 테스트")
    class DeleteBoard {
        
        @Test
        @DisplayName("요청한 회원과 게시글을 작성한 회원이 같다면 게시글을 삭제할 수 있다.")
        void deleteBoardByAuthor() throws Exception {
            //given
            Long boardId = 1L;
            given(apiBoardService.getAuthorIdByBoardId(boardId))
                    .willReturn(1L);

            // when - then
            mvc.perform(delete("/api/boards/{boardId}", boardId)
                            .principal(mockPrincipal)
                    )
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("요청한 회원과 게시글을 작성한 회원이 다르면 게시글을 삭제할 수 없다.")
        void deleteBoardByNotAuthor() throws Exception {
            //given
            Long otherUserId = 2L;
            Long boardId = 1L;
            given(apiBoardService.getAuthorIdByBoardId(boardId))
                    .willReturn(otherUserId);

            // when - then
            mvc.perform(delete("/api/boards/{boardId}", boardId)
                            .principal(mockPrincipal)
                    )
                    .andExpect(status().isUnauthorized());
        }
    }
}