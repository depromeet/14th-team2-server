package com.praise.push.application.service;

import com.praise.push.application.port.in.KeywordUseCase;
import com.praise.push.application.port.in.dto.KeywordResponseDto;
import com.praise.push.application.port.out.LoadKeywordPort;
import com.praise.push.domain.Keyword;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class KeywordService implements KeywordUseCase {
    private final LoadKeywordPort loadKeywordPort;

    @Override
    @Transactional(readOnly = true)
    public List<KeywordResponseDto> getRandomRecommendationKeywords(Integer size) {
        List<Keyword> keywords = loadKeywordPort.loadKeywords();

        if (keywords.size() <= size) {
            return keywords.stream().map(KeywordResponseDto::fromEntity).toList();
        }
        keywords.sort((k1, k2) -> k1.getSelectedCount() - k2.getSelectedCount());
        List<Keyword> recommendationKeywords = keywords.subList(0, size);

        return recommendationKeywords.stream().map(KeywordResponseDto::fromEntity).toList();
    }
}
