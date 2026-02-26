package base.ecs32.top.api.service;

import base.ecs32.top.api.dto.*;
import base.ecs32.top.api.vo.*;
import base.ecs32.top.api.vo.PageResponse;

public interface CourseService {

    // Student API
    void activateCourse(Long userId, CourseActivateRequest request);
    CourseDetailVO getCourseDetail(Long userId, Long courseId);
    LessonDetailVO getLessonDetail(Long userId, Long lessonId);
    void updateLessonProgress(Long userId, Long lessonId, LessonProgressUpdateRequest request);
    void submitAssignment(Long userId, Long lessonId, AssignmentSubmitRequest request);
    PageResponse<MyCourseVO> getMyCourses(Long userId, Integer current, Integer pageSize);
    AssignmentDetailVO getAssignmentDetail(Long userId, Long lessonId);

    // Admin API
    void saveCourse(AdminCourseSaveRequest request);
    void saveChapter(AdminChapterSaveRequest request);
    void saveLesson(AdminLessonSaveRequest request);
    PageResponse<AdminAssignmentVO> getAssignments(AdminAssignmentListRequest request);
    void gradeAssignment(Long assignmentId, Long graderId, AdminAssignmentGradeRequest request);
    AdminUserCourseVO getUserCourses(Long userId);
    void updateUserCourseAccess(Long userId, Long courseId, AdminUserCourseAccessRequest request);
    AdminLessonVO getLesson(Long id);
}
