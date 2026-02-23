package base.ecs32.top.api.service.impl;

import base.ecs32.top.api.dto.*;
import base.ecs32.top.api.service.BlogService;
import base.ecs32.top.api.vo.*;
import base.ecs32.top.blog.dao.*;
import base.ecs32.top.blog.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final base.ecs32.top.blog.dao.CategoryMapper categoryMapper;
    private final base.ecs32.top.blog.dao.TagMapper tagMapper;
    private final base.ecs32.top.blog.dao.PostMapper postMapper;
    private final base.ecs32.top.blog.dao.PostTagMapper postTagMapper;

    @Override
    @Transactional
    public void saveCategory(CategorySaveRequest request) {
        Category category = new Category();
        category.setId(request.getId());
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "ACTIVE");

        if (category.getId() == null) {
            category.setCreateTime(LocalDateTime.now());
            category.setUpdateTime(LocalDateTime.now());
            categoryMapper.insert(category);
        } else {
            category.setUpdateTime(LocalDateTime.now());
            categoryMapper.updateById(category);
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    @Override
    public CategoryVO getCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return null;
        }
        return toCategoryVO(category);
    }

    @Override
    public List<CategoryVO> listCategories(String status) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(Category::getStatus, status);
        }
        wrapper.orderByAsc(Category::getSortOrder);
        List<Category> categories = categoryMapper.selectList(wrapper);
        return categories.stream().map(this::toCategoryVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveTag(TagSaveRequest request) {
        Tag tag = new Tag();
        tag.setId(request.getId());
        tag.setName(request.getName());
        tag.setSlug(request.getSlug());
        tag.setDescription(request.getDescription());
        tag.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "ACTIVE");

        if (tag.getId() == null) {
            tag.setCreateTime(LocalDateTime.now());
            tag.setUpdateTime(LocalDateTime.now());
            tagMapper.insert(tag);
        } else {
            tag.setUpdateTime(LocalDateTime.now());
            tagMapper.updateById(tag);
        }
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        tagMapper.deleteById(id);
    }

    @Override
    public TagVO getTag(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            return null;
        }
        return toTagVO(tag);
    }

    @Override
    public List<TagVO> listTags(String status) {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(Tag::getStatus, status);
        }
        wrapper.orderByDesc(Tag::getCreateTime);
        List<Tag> tags = tagMapper.selectList(wrapper);
        return tags.stream().map(this::toTagVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void savePost(PostSaveRequest request) {
        Post post = new Post();
        post.setId(request.getId());
        post.setTitle(request.getTitle());
        post.setSlug(request.getSlug());
        post.setExcerpt(request.getExcerpt());
        post.setContent(request.getContent());
        post.setContentHtml(request.getContentHtml());
        post.setExcerptText(request.getExcerptText());
        post.setContentText(request.getContentText());
        post.setCoverImage(request.getCoverImage());
        post.setAuthorId(request.getAuthorId());
        post.setCategoryId(request.getCategoryId());
        post.setStatus(StringUtils.hasText(request.getStatus()) ? request.getStatus() : "DRAFT");
        post.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);

        boolean isNew = post.getId() == null;
        LocalDateTime now = LocalDateTime.now();

        if (isNew) {
            post.setCreateTime(now);
            post.setUpdateTime(now);
            post.setViewCount(0);
            post.setLikeCount(0);
            post.setCommentCount(0);

            // Set published at if status is PUBLISHED
            if ("PUBLISHED".equals(post.getStatus())) {
                post.setPublishedAt(now);
            }

            postMapper.insert(post);
        } else {
            post.setUpdateTime(now);

            // Handle published at when transitioning to PUBLISHED
            Post existingPost = postMapper.selectById(post.getId());
            if (existingPost != null && !existingPost.getStatus().equals("PUBLISHED") && "PUBLISHED".equals(post.getStatus())) {
                post.setPublishedAt(now);
            }

            postMapper.updateById(post);

            // Delete existing post-tag relations
            LambdaQueryWrapper<PostTag> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(PostTag::getPostId, post.getId());
            postTagMapper.delete(deleteWrapper);
        }

        // Save post-tag relations
        if (!CollectionUtils.isEmpty(request.getTagIds())) {
            for (Long tagId : request.getTagIds()) {
                PostTag postTag = new PostTag();
                postTag.setPostId(post.getId());
                postTag.setTagId(tagId);
                postTag.setCreateTime(now);
                postTagMapper.insert(postTag);
            }
        }
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        // Delete post-tag relations first
        LambdaQueryWrapper<PostTag> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(PostTag::getPostId, id);
        postTagMapper.delete(deleteWrapper);

        // Delete post
        postMapper.deleteById(id);
    }

    @Override
    public PostVO getPost(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            return null;
        }
        return toPostVO(post);
    }

    @Override
    public PostVO getPostBySlug(String slug) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getSlug, slug);
        Post post = postMapper.selectOne(wrapper);
        if (post == null) {
            return null;
        }
        return toPostVO(post);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post != null) {
            post.setViewCount((post.getViewCount() != null ? post.getViewCount() : 0) + 1);
            postMapper.updateById(post);
        }
    }

    @Override
    @Transactional
    public void incrementLikeCount(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post != null) {
            post.setLikeCount((post.getLikeCount() != null ? post.getLikeCount() : 0) + 1);
            postMapper.updateById(post);
        }
    }

    @Override
    public PageResponse<PostVO> listPosts(PostListRequest request) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getStatus())) {
            wrapper.eq(Post::getStatus, request.getStatus());
        }

        if (request.getCategoryId() != null) {
            wrapper.eq(Post::getCategoryId, request.getCategoryId());
        }

        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w.like(Post::getTitle, request.getKeyword())
                    .or()
                    .like(Post::getExcerpt, request.getKeyword()));
        }

        wrapper.orderByDesc(Post::getIsFeatured);
        wrapper.orderByDesc(Post::getPublishedAt);
        wrapper.orderByDesc(Post::getCreateTime);

        Page<Post> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<Post> resultPage = postMapper.selectPage(page, wrapper);

        List<PostVO> postVOs = resultPage.getRecords().stream()
                .map(this::toPostVO)
                .collect(Collectors.toList());

        return PageResponse.of(postVOs, resultPage.getTotal(), request.getPageNum(), request.getPageSize());
    }

    private CategoryVO toCategoryVO(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setSlug(category.getSlug());
        vo.setDescription(category.getDescription());
        vo.setSortOrder(category.getSortOrder());
        vo.setStatus(category.getStatus());
        vo.setCreateTime(category.getCreateTime());
        vo.setUpdateTime(category.getUpdateTime());
        return vo;
    }

    private TagVO toTagVO(Tag tag) {
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setName(tag.getName());
        vo.setSlug(tag.getSlug());
        vo.setDescription(tag.getDescription());
        vo.setStatus(tag.getStatus());
        vo.setCreateTime(tag.getCreateTime());
        vo.setUpdateTime(tag.getUpdateTime());
        return vo;
    }

    private PostVO toPostVO(Post post) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setSlug(post.getSlug());
        vo.setExcerpt(post.getExcerpt());
        vo.setContent(post.getContent());
        vo.setContentHtml(post.getContentHtml());
        vo.setExcerptText(post.getExcerptText());
        vo.setContentText(post.getContentText());
        vo.setCoverImage(post.getCoverImage());
        vo.setAuthorId(post.getAuthorId());
        vo.setCategoryId(post.getCategoryId());
        vo.setStatus(post.getStatus());
        vo.setViewCount(post.getViewCount());
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setIsFeatured(post.getIsFeatured());
        vo.setPublishedAt(post.getPublishedAt());
        vo.setCreateTime(post.getCreateTime());
        vo.setUpdateTime(post.getUpdateTime());

        // Load category
        if (post.getCategoryId() != null) {
            Category category = categoryMapper.selectById(post.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        // Load tags
        LambdaQueryWrapper<PostTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(PostTag::getPostId, post.getId());
        List<PostTag> postTags = postTagMapper.selectList(tagWrapper);

        if (!CollectionUtils.isEmpty(postTags)) {
            List<Long> tagIds = postTags.stream()
                    .map(PostTag::getTagId)
                    .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(tagIds)) {
                List<Tag> tags = tagMapper.selectBatchIds(tagIds);
                vo.setTags(tags.stream().map(this::toTagVO).collect(Collectors.toList()));
            }
        }

        return vo;
    }
}
