package base.ecs32.top.api.controller;

import base.ecs32.top.api.dto.*;
import base.ecs32.top.api.service.BlogService;
import base.ecs32.top.api.vo.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    // ========== Category Endpoints ==========

    @PostMapping("/categories")
    public void saveCategory(@Valid @RequestBody CategorySaveRequest request) {
        blogService.saveCategory(request);
    }

    @DeleteMapping("/categories/{cid}")
    public void deleteCategory(@PathVariable("cid") Integer cid) {
        blogService.deleteCategory(cid);
    }

    @GetMapping("/categories/{cid}")
    public CategoryVO getCategory(@PathVariable("cid") Integer cid) {
        return blogService.getCategory(cid);
    }

    @GetMapping("/categories")
    public List<CategoryVO> listCategories() {
        return blogService.listCategories();
    }

    // ========== Tag Endpoints ==========

    @PostMapping("/tags")
    public void saveTag(@Valid @RequestBody TagSaveRequest request) {
        blogService.saveTag(request);
    }

    @DeleteMapping("/tags/{id}")
    public void deleteTag(@PathVariable("id") Integer id) {
        blogService.deleteTag(id);
    }

    @GetMapping("/tags/{id}")
    public TagVO getTag(@PathVariable("id") Integer id) {
        return blogService.getTag(id);
    }

    @GetMapping("/tags")
    public List<TagVO> listTags() {
        return blogService.listTags();
    }

    // ========== Post Endpoints ==========

    @PostMapping("/posts")
    public void savePost(@Valid @RequestBody PostSaveRequest request) {
        blogService.savePost(request);
    }

    @DeleteMapping("/posts/{pid}")
    public void deletePost(@PathVariable("pid") Integer pid) {
        blogService.deletePost(pid);
    }

    @GetMapping("/posts/{pid}")
    public PostVO getPost(@PathVariable("pid") Integer pid) {
        return blogService.getPost(pid);
    }

    @GetMapping("/posts/slug/{slug}")
    public PostVO getPostBySlug(@PathVariable("slug") String slug) {
        return blogService.getPostBySlug(slug);
    }

    @PostMapping("/posts/list")
    public PageResponse<PostVO> listPosts(@RequestBody PostListRequest request) {
        return blogService.listPosts(request);
    }
}
