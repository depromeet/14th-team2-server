package com.praise.push.application.service;

import com.praise.push.application.port.in.CreatePostCommand;
import com.praise.push.application.port.in.PostUseCase;
import com.praise.push.application.port.in.UpdatePostCommand;
import com.praise.push.application.port.in.dto.PostSummaryResponseDto;
import com.praise.push.application.port.out.*;
import com.praise.push.domain.Keyword;
import com.praise.push.domain.Post;
import com.praise.push.common.constant.Names;
import com.praise.push.domain.User;
import com.praise.push.domain.model.PostWithCommentCount;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService implements PostUseCase {
    private final RecordPostPort recordPostPort;
    private final RecordImagePort recordImagePort;
    private final LoadPostPort loadPostPort;
    private final LoadKeywordPort keywordPort;
    private final LoadUserPort loadUserPort;
    private final RecordCommentPort recordCommentPort;

    @Override
    public boolean createPost(Long userId, CreatePostCommand command) {
        String imageUrl = recordImagePort.uploadImage(Names.POST_FOLDER_NAME.getName(), command.getImage());
        Keyword keyword = keywordPort.loadKeywordById(command.getKeywordId());
        User user = loadUserPort.loadUserById(userId);

        /**
         * TODO:
         * DTO 검증
         * - content 글자수 제한: 40자
         *
         * visible이 2개이면
         * - 새로운 Post 생성 불가
         */

        Post post = Post.builder()
                .content(command.getContent())
                .imageUrl(imageUrl)
                .keyword(keyword)
                .user(user)
                .visible(false)
                .isRead(false)
                .build();

        recordPostPort.createPost(post);
        return true;
    }

    @Override
    public Page<PostSummaryResponseDto> getVisiblePosts(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostWithCommentCount> posts = loadPostPort.loadVisiblePosts(pageable);

        return posts.map(PostSummaryResponseDto::fromVisibleEntity);
    }

    @Override
    public List<PostSummaryResponseDto> getInvisiblePosts() {
        List<PostWithCommentCount> posts = loadPostPort.loadInvisiblePosts();

        if (posts == null) {
            return null;
        }

        return posts.stream()
                .map(PostSummaryResponseDto::fromInvisibleEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Post findPost(Long postId) {
        return loadPostPort.findPost(postId);
    }

    @Transactional
    @Override
    public boolean deletePost(Long postId) {
        recordCommentPort.deleteCommentsByPostId(postId);
        recordPostPort.deletePost(postId);
        return true;
    }

    @Override
    public boolean updatePost(Long postId, UpdatePostCommand command) {
        Post post = Post.builder()
                .content(command.getContent())
                .imageUrl(command.getImageUrl())
                .keyword(Keyword.builder().keyword(command.getKeyword()).build())
                .build();

        recordPostPort.updatePost(postId, post);
        return true;
    }

    @Override
    public void updatePostReadState(Long postId) {
        Post post = Post.builder()
                .isRead(true)
                .build();

        recordPostPort.updatePostReadState(postId, post);
    }

    @Transactional
    public void updateOpenStatus() {
        var posts = loadPostPort.findAll();
        var oneDayAgo = LocalDateTime.now().minusDays(1);

        posts.stream().filter(post -> post.getCreatedDate().isAfter(oneDayAgo))
            .forEach(post -> {
                post.changeOpen(true);
                recordPostPort.createPost(post);
            });
    }
}
