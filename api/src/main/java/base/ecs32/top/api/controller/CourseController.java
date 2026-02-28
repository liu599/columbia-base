package base.ecs32.top.api.controller;

import base.ecs32.top.api.dto.*;
import base.ecs32.top.api.service.CourseService;
import base.ecs32.top.api.vo.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * P0: 激活/兑换/购买课程
     * TODO: 后续会更新成使用 course_id 而不是 product_id
     */
    @PostMapping("/courses/{product_id}/activate")
    public void activateCourse(
            @PathVariable("product_id") Long productId,
            @Valid @RequestBody CourseActivateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        // TODO: 后续会移除 productId 参数（改为路径参数 course_id）
        courseService.activateCourse(userId, request);
    }

    /**
     * P0: 获取课程详情及大纲目录
     */
    @PostMapping("/courses/{course_id}")
    public CourseDetailVO getCourseDetail(
            @PathVariable("course_id") Long courseId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return courseService.getCourseDetail(userId, courseId);
    }

    /**
     * P0: 获取单一课时的详细内容
     */
    @PostMapping("/lessons/{lesson_id}")
    public LessonDetailVO getLessonDetail(
            @PathVariable("lesson_id") Long lessonId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return courseService.getLessonDetail(userId, lessonId);
    }

    /**
     * P0: 上报/更新学习进度
     */
    @PostMapping("/lessons/{lesson_id}/progress")
    public void updateLessonProgress(
            @PathVariable("lesson_id") Long lessonId,
            @Valid @RequestBody LessonProgressUpdateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        courseService.updateLessonProgress(userId, lessonId, request);
    }

    /**
     * P0: 提交作业
     */
    @PostMapping("/assignments/{lesson_id}/submit")
    public void submitAssignment(
            @PathVariable("lesson_id") Long lessonId,
            @Valid @RequestBody AssignmentSubmitRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        courseService.submitAssignment(userId, lessonId, request);
    }

    /**
     * P1: 获取"我的课程"列表
     */
    @PostMapping("/my-courses")
    public PageResponse<MyCourseVO> getMyCourses(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return courseService.getMyCourses(userId, current, pageSize);
    }

    /**
     * P1: 查询某次作业的当前状态、得分及老师评语
     */
    @PostMapping("/assignments/{lesson_id}")
    public AssignmentDetailVO getAssignmentDetail(
            @PathVariable("lesson_id") Long lessonId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return courseService.getAssignmentDetail(userId, lessonId);
    }
}
