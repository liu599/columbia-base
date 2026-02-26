package base.ecs32.top.api.controller;

import base.ecs32.top.api.advice.BusinessException;
import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.dto.*;
import base.ecs32.top.api.service.CourseService;
import base.ecs32.top.api.util.OssUtils;
import base.ecs32.top.api.vo.*;
import base.ecs32.top.dao.UserMapper;
import base.ecs32.top.entity.User;
import base.ecs32.top.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;
    private final UserMapper userMapper;
    private final OssUtils ossUtils;

    /**
     * P0: 创建或更新课程基础元数据
     */
    @PostMapping("/courses")
    public void saveCourse(@Valid @RequestBody AdminCourseSaveRequest request) {
        courseService.saveCourse(request);
    }

    /**
     * P0: 管理课程的大纲结构
     */
    @PostMapping("/chapters")
    public void saveChapter(@Valid @RequestBody AdminChapterSaveRequest request) {
        courseService.saveChapter(request);
    }

    /**
     * P0: 创建或更新课时内容
     */
    @PostMapping("/lessons")
    public void saveLesson(@Valid @RequestBody AdminLessonSaveRequest request) {
        courseService.saveLesson(request);
    }

    /**
     * P0: 根据 id 获取课时内容
     */
    @PostMapping("/lessons/get")
    public AdminLessonVO getLesson(@Valid @RequestBody AdminLessonGetRequest request) {
        return courseService.getLesson(request.getId());
    }

    /**
     * P0: 分页获取待批改或所有状态的作业列表
     */
    @PostMapping("/assignments")
    public PageResponse<AdminAssignmentVO> getAssignments(
            @RequestBody AdminAssignmentListRequest request) {
        return courseService.getAssignments(request);
    }

    /**
     * P0: 批改作业
     */
    @PostMapping("/assignments/{assignment_id}/grade")
    public void gradeAssignment(
            @PathVariable("assignment_id") Long assignmentId,
            @Valid @RequestBody AdminAssignmentGradeRequest request,
            HttpServletRequest httpRequest) {
        Long graderId = (Long) httpRequest.getAttribute("userId");
        courseService.gradeAssignment(assignmentId, graderId, request);
    }

    /**
     * P1: 获取上传文件的临时凭证
     */
    @PostMapping("/upload/presigned-url")
    public Map<String, Object> getPresignedUrl(
            @RequestParam(value = "objectName") String objectName,
            @RequestParam(value = "contentType", required = false) String contentType) {
        // 注意：此方法需要 OssUtils 支持 generateUploadPresignedUrl
        // 当前简化版本返回错误提示
        Map<String, Object> result = new HashMap<>();
        result.put("error", "请使用文件上传接口 /api/v1/file/upload 直接上传文件");
        return result;
    }

    /**
     * P1: 查看某位学生的拥有的课程及详细进度
     */
    @PostMapping("/users/{user_id}/courses")
    public AdminUserCourseVO getUserCourses(
            @PathVariable("user_id") Long userId,
            HttpServletRequest httpRequest) {
        // Verify admin permission
        Long adminId = (Long) httpRequest.getAttribute("userId");
        User admin = userMapper.selectById(adminId);
        if (admin == null || UserRole.fromLevel(admin.getRoleLevel()).getLevel() < UserRole.INTERNAL_ADMIN.getLevel()) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED, "无权限访问此接口");
        }

        return courseService.getUserCourses(userId);
    }

    /**
     * P1: 人工干预学生的课程权限
     */
    @PostMapping("/users/{user_id}/courses/{course_id}/access")
    public void updateUserCourseAccess(
            @PathVariable("user_id") Long userId,
            @PathVariable("course_id") Long courseId,
            @Valid @RequestBody AdminUserCourseAccessRequest request,
            HttpServletRequest httpRequest) {
        // Verify admin permission
        Long adminId = (Long) httpRequest.getAttribute("userId");
        User admin = userMapper.selectById(adminId);
        if (admin == null || UserRole.fromLevel(admin.getRoleLevel()).getLevel() < UserRole.INTERNAL_ADMIN.getLevel()) {
            throw new BusinessException(ResultCode.PERMISSION_DENIED, "无权限访问此接口");
        }

        courseService.updateUserCourseAccess(userId, courseId, request);
    }
}
