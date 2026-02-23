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

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable("id") Long id) {
        blogService.deleteCategory(id);
    }

    @GetMapping("/categories/{id}")
    public CategoryVO getCategory(@PathVariable("id") Long id) {
        return blogService.getCategory(id);
    }

    @GetMapping("/categories")
    public List<CategoryVO> listCategories(
            @RequestParam(value = "status", required = false) String status) {
        return blogService.listCategories(status);
    }

    // ========== Tag Endpoints ==========

    @PostMapping("/tags")
    public void saveTag(@Valid @RequestBody TagSaveRequest request) {
        blogService.saveTag(request);
    }

    @DeleteMapping("/tags/{id}")
    public void deleteTag(@PathVariable("id") Long id) {
        blogService.deleteTag(id);
    }

    @GetMapping("/tags/{id}")
    public TagVO getTag(@PathVariable("id") Long id) {
        return blogService.getTag(id);
    }

    @GetMapping("/tags")
    public List<TagVO> listTags(
            @RequestParam(value = "status", required = false) String status) {
        return blogService.listTags(status);
    }

    // ========== Post Endpoints ==========

    @PostMapping("/posts")
    public void savePost(@Valid @RequestBody PostSaveRequest request) {
        blogService.savePost(request);
    }

    @DeleteMapping("/posts/{id}")
    public void deletePost(@PathVariable("id") Long id) {
        blogService.deletePost(id);
    }

    @GetMapping("/posts/{id}")
    public PostVO getPost(@PathVariable("id") Long id) {
        return blogService.getPost(id);
    }

    @GetMapping("/posts/slug/{slug}")
    public PostVO getPostBySlug(@PathVariable("slug") String slug) {
        return blogService.getPostBySlug(slug);
    }

    @PostMapping("/posts/{id}/view")
    public void incrementViewCount(@PathVariable("id") Long id) {
        blogService.incrementViewCount(id);
    }

    @PostMapping("/posts/{id}/like")
    public void incrementLikeCount(@PathVariable("id") Long id) {
        blogService.incrementLikeCount(id);
    }

    @PostMapping("/posts/list")
    public PageResponse<PostVO> listPosts(@RequestBody PostListRequest request) {
        return blogService.listPosts(request);
    }
}
