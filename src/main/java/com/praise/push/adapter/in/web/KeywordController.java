package com.praise.push.adapter.in.web;

import com.praise.push.application.port.in.KeywordUseCase;
import com.praise.push.application.port.in.dto.KeywordResponseDto;
import com.praise.push.common.error.exception.ValidationFailException;
import com.praise.push.common.model.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/praise-up/api/v1")
@Tag(name = "keywords", description = "Keyword API")
@CrossOrigin(origins = "*", allowedHeaders = "*")
class KeywordController {
    private static final String KEYWORD_SIZE_INVALID_MESSAGE = "size must be at least zero.";

    private final KeywordUseCase keywordUseCase;

    /**
     * get recommendation keywords as many as size
     */
    @Operation(summary = "칭찬 키워드 조회")
    @ApiResponse(responseCode = "200", description = "칭찬 키워드 조회 성공")
    @GetMapping("/keywords/recommendation")
    ResponseEntity<List<KeywordResponseDto>> recommendationKeywords(
            @RequestParam("size") Integer size
    ) {
        if (size < 0) {
            throw new ValidationFailException(KEYWORD_SIZE_INVALID_MESSAGE);
        }
        return ResponseDto.ok(keywordUseCase.getRandomRecommendationKeywords(size));
    }
}
