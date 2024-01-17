package com.praise.push.application.port.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    /**
     * 게시글 본문
     */
    private String content;

    /**
     * 게시글 이미지
     */
    private String imageUrl;

    /**
     * 키워드
     */
    private String keyword;

    /**
     * 게시글 공개 여부
     */
    private Boolean visible;

    /**
     * 게시글 읽음 여부
     */
    private Boolean isRead;

    /**
     * 게시글 등록 일자
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date postCreatedDate;
}
