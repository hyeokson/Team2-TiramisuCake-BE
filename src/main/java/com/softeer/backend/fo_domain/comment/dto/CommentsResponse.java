package com.softeer.backend.fo_domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softeer.backend.fo_domain.comment.constant.CommentNickname;
import com.softeer.backend.fo_domain.comment.constant.ExpectationComment;
import com.softeer.backend.fo_domain.comment.domain.Comment;
import com.softeer.backend.fo_domain.comment.util.ScrollPaginationUtil;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@Getter
public class CommentsResponse {
    public static final int LAST_CURSOR = -1;

    private int nextCursor;

    private int totalComments;

    private List<CommentResponse> comments;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class CommentResponse {

        private Boolean isMine;

        private String nickName;

        private String comment;
    }

    public static CommentsResponse of(ScrollPaginationUtil<Comment> commentsScroll, Integer userId) {
        if (commentsScroll.isLastScroll()) {
            return CommentsResponse.newLastScroll(commentsScroll.getCurrentScrollItems(), userId);
        }
        return CommentsResponse.newScrollHasNext(commentsScroll.getCurrentScrollItems(), commentsScroll.getNextCursor().getId(),
                userId);
    }

    // 마지막 스크롤일 때의 응답값을 구성하는 메서드
    // nextCursor 값을 -1로 설정한다.
    private static CommentsResponse newLastScroll(List<Comment> commentsScroll, Integer userId) {
        return newScrollHasNext(commentsScroll, LAST_CURSOR, userId);
    }

    // 마지막 스크롤이 아닐 때의 응답값을 구성하는 메서드
    private static CommentsResponse newScrollHasNext(List<Comment> commentsScroll, int nextCursor,
                                                     Integer userId) {
        return CommentsResponse.builder()
                .nextCursor(nextCursor)
                .totalComments(commentsScroll.size())
                .comments(getContents(commentsScroll, userId))
                .build();
    }

    // CommentResponse를 생성하여 반환하는 메서드
    // 유저가 로그인을 한 상태에서 자신의 댓글이 응답에 포함될 경우,
    // isMine 변수값을 true로, nickname의 접미사에 '(나)'를 붙여서 응답을 구성한다.
    private static List<CommentResponse> getContents(List<Comment> commentsScroll, Integer userId) {
        return commentsScroll.stream()
                .map(_comment -> {
                    boolean isMine = false;
                    String nickname = _comment.getNickname();
                    String comment = _comment.getExpectationComment().getComment();

                    if(userId != null && _comment.getUserId() != null &&
                            _comment.getUserId().equals(userId)){
                        isMine = true;
                        nickname = nickname + CommentNickname.MY_NICKNAME_SUFFIX;
                    }

                    return CommentResponse.builder()
                            .isMine(isMine)
                            .nickName(nickname)
                            .comment(comment)
                            .build();
                })
                .toList();

    }
}
