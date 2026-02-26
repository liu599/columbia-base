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
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final base.ecs32.top.blog.dao.CategoryMapper categoryMapper;
    private final base.ecs32.top.blog.dao.TagMapper tagMapper;
    private final base.ecs32.top.blog.dao.PostMapper postMapper;

    @Override
    @Transactional
    public void saveCategory(CategorySaveRequest request) {
        Category category = new Category();
        category.setCid(request.getCid());
        category.setId(request.getId());
        category.setCname(request.getCname());
        category.setClink(request.getClink());
        category.setCinfo(request.getCinfo());

        if (request.getCid() == null) {
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateById(category);
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Integer cid) {
        categoryMapper.deleteById(cid);
    }

    @Override
    public CategoryVO getCategory(Integer cid) {
        Category category = categoryMapper.selectById(cid);
        if (category == null) {
            return null;
        }
        return toCategoryVO(category);
    }

    @Override
    public List<CategoryVO> listCategories() {
        List<Category> categories = categoryMapper.selectList(null);
        return categories.stream().map(this::toCategoryVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveTag(TagSaveRequest request) {
        Tag tag = new Tag();
        tag.setId(request.getId());
        tag.setTagId(request.getTagId());
        tag.setTagLink(request.getTagLink());
        tag.setTagName(request.getTagName());

        if (tag.getId() == null) {
            tag.setCreatedAt(java.time.LocalDate.now());
            tag.setUpdatedAt(java.time.LocalDate.now());
            tagMapper.insert(tag);
        } else {
            tag.setUpdatedAt(java.time.LocalDate.now());
            tagMapper.updateById(tag);
        }
    }

    @Override
    @Transactional
    public void deleteTag(Integer id) {
        tagMapper.deleteById(id);
    }

    @Override
    public TagVO getTag(Integer id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            return null;
        }
        return toTagVO(tag);
    }

    @Override
    public List<TagVO> listTags() {
        List<Tag> tags = tagMapper.selectList(null);
        return tags.stream().map(this::toTagVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void savePost(PostSaveRequest request) {
        // 检查 poid 是否已存在
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getPoid, request.getPoid());
        Post existingPost = postMapper.selectOne(queryWrapper);

        if (existingPost != null) {
            // poid 已存在，执行更新
            updatePost(request);
        } else {
            // poid 不存在，执行插入
            Post post = new Post();
            post.setPoid(request.getPoid());
            post.setAuthor(request.getAuthor());
            post.setCategory(request.getCategory());
            post.setBody(request.getBody());
            post.setPtitle(request.getPtitle());
            post.setSlug(request.getSlug());
            post.setPassword(request.getPassword() != null ? request.getPassword() : "");

            long now = System.currentTimeMillis() / 1000L;
            post.setCreatedAt(now);
            post.setModifiedAt(now);
            postMapper.insert(post);
        }
    }

    @Override
    @Transactional
    public void updatePost(PostSaveRequest request) {
        Post post = new Post();
        post.setPoid(request.getPoid());
        post.setAuthor(request.getAuthor());
        post.setCategory(request.getCategory());
        post.setBody(request.getBody());
        post.setPtitle(request.getPtitle());
        post.setSlug(request.getSlug());
        post.setPassword(request.getPassword() != null ? request.getPassword() : "");

        long now = System.currentTimeMillis() / 1000L;
        post.setModifiedAt(now);

        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getPoid, request.getPoid());
        postMapper.update(post, wrapper);
    }

    @Override
    @Transactional
    public void deletePost(Integer pid) {
        postMapper.deleteById(pid);
    }

    @Override
    public PostVO getPost(Integer pid) {
        Post post = postMapper.selectById(pid);
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
    public PageResponse<PostVO> listPosts(PostListRequest request) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(request.getCategory())) {
            wrapper.eq(Post::getCategory, request.getCategory());
        }

        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.and(w -> w.like(Post::getPtitle, request.getKeyword())
                    .or()
                    .like(Post::getBody, request.getKeyword()));
        }

        wrapper.orderByDesc(Post::getCreatedAt);

        Page<Post> page = new Page<>(request.getPageNum(), request.getPageSize());
        IPage<Post> resultPage = postMapper.selectPage(page, wrapper);

        List<PostVO> postVOs = resultPage.getRecords().stream()
                .map(this::toPostVO)
                .collect(Collectors.toList());

        return PageResponse.of(postVOs, resultPage.getTotal(), request.getPageNum(), request.getPageSize());
    }

    private CategoryVO toCategoryVO(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setCid(category.getCid());
        vo.setId(category.getId());
        vo.setCname(category.getCname());
        vo.setClink(category.getClink());
        vo.setCinfo(category.getCinfo());
        return vo;
    }

    private TagVO toTagVO(Tag tag) {
        TagVO vo = new TagVO();
        vo.setId(tag.getId());
        vo.setTagId(tag.getTagId());
        vo.setTagLink(tag.getTagLink());
        vo.setTagName(tag.getTagName());
        vo.setCreatedAt(tag.getCreatedAt());
        vo.setUpdatedAt(tag.getUpdatedAt());
        vo.setDeletedAt(tag.getDeletedAt());
        return vo;
    }

    private PostVO toPostVO(Post post) {
        PostVO vo = new PostVO();
        vo.setPid(post.getPid());
        vo.setPoid(post.getPoid());
        vo.setAuthor(post.getAuthor());
        vo.setCategory(post.getCategory());
        vo.setBody(post.getBody());
        vo.setPtitle(post.getPtitle());
        vo.setSlug(post.getSlug());
        vo.setPassword(post.getPassword());
        vo.setCreatedAt(post.getCreatedAt());
        vo.setModifiedAt(post.getModifiedAt());
        return vo;
    }
}
