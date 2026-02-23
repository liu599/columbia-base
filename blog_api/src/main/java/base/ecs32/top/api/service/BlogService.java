package base.ecs32.top.api.service;

import base.ecs32.top.api.dto.*;
import base.ecs32.top.api.vo.*;

import java.util.List;

public interface BlogService {

    // Category methods
    void saveCategory(CategorySaveRequest request);

    void deleteCategory(Long id);

    CategoryVO getCategory(Long id);

    List<CategoryVO> listCategories(String status);

    // Tag methods
    void saveTag(TagSaveRequest request);

    void deleteTag(Long id);

    TagVO getTag(Long id);

    List<TagVO> listTags(String status);

    // Post methods
    void savePost(PostSaveRequest request);

    void deletePost(Long id);

    PostVO getPost(Long id);

    PostVO getPostBySlug(String slug);

    void incrementViewCount(Long postId);

    void incrementLikeCount(Long postId);

    PageResponse<PostVO> listPosts(PostListRequest request);
}
