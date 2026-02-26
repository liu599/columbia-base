package base.ecs32.top.api.service;

import base.ecs32.top.api.dto.*;
import base.ecs32.top.api.vo.*;

import java.util.List;

public interface BlogService {

    // Category methods
    void saveCategory(CategorySaveRequest request);

    void deleteCategory(Integer cid);

    CategoryVO getCategory(Integer cid);

    List<CategoryVO> listCategories();

    // Tag methods
    void saveTag(TagSaveRequest request);

    void deleteTag(Integer id);

    TagVO getTag(Integer id);

    List<TagVO> listTags();

    // Post methods
    void savePost(PostSaveRequest request);

    void deletePost(Integer pid);

    PostVO getPost(Integer pid);

    PostVO getPostBySlug(String slug);

    PageResponse<PostVO> listPosts(PostListRequest request);
}
