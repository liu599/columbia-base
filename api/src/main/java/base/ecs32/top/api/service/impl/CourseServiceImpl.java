package base.ecs32.top.api.service.impl;

import base.ecs32.top.api.advice.BusinessException;
import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.dto.*;
import base.ecs32.top.api.service.CourseService;
import base.ecs32.top.api.service.FileService;
import base.ecs32.top.api.vo.*;
import base.ecs32.top.dao.*;
import base.ecs32.top.entity.File;
import base.ecs32.top.entity.*;
import base.ecs32.top.enums.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    private final CourseMapper courseMapper;
    private final ChapterMapper chapterMapper;
    private final LessonMapper lessonMapper;
    private final UserCourseMapper userCourseMapper;
    private final UserLessonProgressMapper userLessonProgressMapper;
    private final UserAssignmentMapper userAssignmentMapper;
    private final ActivationCodeMapper activationCodeMapper;
    private final UserMapper userMapper;
    private final FileService fileService;
    private final FileMapper fileMapper;
    private final ObjectMapper objectMapper;
    private final ProductMapper productMapper;

    private static final long PRE_SIGNED_URL_EXPIRATION = 3600; // 1 hour

    // ==================== Student API ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateCourse(Long userId, CourseActivateRequest request) {
        // Validate activation code
        ActivationCode code = activationCodeMapper.selectOne(
                new LambdaQueryWrapper<ActivationCode>()
                        .eq(ActivationCode::getCode, request.getCode())
                        .eq(ActivationCode::getStatus, ActivationCodeStatus.UNUSED)
        );

        if (code == null) {
            throw new BusinessException(ResultCode.ACTIVATION_CODE_NOT_FOUND, "激活码不存在");
        }

        if (code.getStatus() != ActivationCodeStatus.UNUSED) {
            throw new BusinessException(ResultCode.ACTIVATION_CODE_ALREADY_USED, "激活码已失效");
        }

        // Check if product exists (TODO: 后续会更新成使用 course_id 而不是 product_id)
        Product product = productMapper.selectById(code.getProductId());
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND, "关联产品不存在");
        }

        // Find the course associated with this product
        Course course = courseMapper.selectOne(
                new LambdaQueryWrapper<Course>()
                        .eq(Course::getProductId, product.getId())
                        .orderByDesc(Course::getId)
                        .last("LIMIT 1")
        );

        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND, "产品关联的课程不存在");
        }

        if (!CourseStatus.PUBLISHED.name().equals(course.getStatus())) {
            throw new BusinessException(ResultCode.COURSE_NOT_PUBLISHED, "课程未发布");
        }

        // Check if already activated
        UserCourse existing = userCourseMapper.selectOne(
                new LambdaQueryWrapper<UserCourse>()
                        .eq(UserCourse::getUserId, userId)
                        .eq(UserCourse::getCourseId, course.getId())
        );

        if (existing != null) {
            throw new BusinessException(ResultCode.COURSE_ALREADY_ACTIVATED, "课程已激活");
        }

        // Activate course
        UserCourse userCourse = new UserCourse();
        userCourse.setUserId(userId);
        userCourse.setCourseId(course.getId());
        userCourse.setAccessStatus(AccessStatus.ACTIVE.name());
        userCourse.setProgressPercent(0);
        userCourse.setActivatedAt(LocalDateTime.now());
        userCourse.setValidUntil(null); // Buyout model, no expiration
        userCourse.setCreateTime(LocalDateTime.now());
        userCourse.setUpdateTime(LocalDateTime.now());
        userCourseMapper.insert(userCourse);

        // Mark activation code as used
        code.setStatus(ActivationCodeStatus.USED);
        code.setUserId(userId);
        code.setUsedTime(LocalDateTime.now());
        activationCodeMapper.updateById(code);
    }

    @Override
    public CourseDetailVO getCourseDetail(Long userId, Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ResultCode.COURSE_NOT_FOUND, "课程不存在");
        }

        // Check if user has access
        UserCourse userCourse = userCourseMapper.selectOne(
                new LambdaQueryWrapper<UserCourse>()
                        .eq(UserCourse::getUserId, userId)
                        .eq(UserCourse::getCourseId, courseId)
        );

        if (userCourse == null || !AccessStatus.ACTIVE.name().equals(userCourse.getAccessStatus())) {
            throw new BusinessException(ResultCode.COURSE_NOT_ACTIVATED, "课程未激活或已过期");
        }

        // Get chapters with lessons
        List<Chapter> chapters = chapterMapper.selectList(
                new LambdaQueryWrapper<Chapter>()
                        .eq(Chapter::getCourseId, courseId)
                        .orderByAsc(Chapter::getSortOrder)
        );

        List<ChapterLessonsVO> chapterLessonsList = chapters.stream()
                .map(chapter -> {
                    List<Lesson> lessons = lessonMapper.selectList(
                            new LambdaQueryWrapper<Lesson>()
                                    .eq(Lesson::getChapterId, chapter.getId())
                                    .orderByAsc(Lesson::getSortOrder)
                    );

                    List<LessonSimpleVO> lessonVOs = lessons.stream()
                            .map(lesson -> {
                                UserLessonProgress progress = userLessonProgressMapper.selectOne(
                                        new LambdaQueryWrapper<UserLessonProgress>()
                                                .eq(UserLessonProgress::getUserId, userId)
                                                .eq(UserLessonProgress::getLessonId, lesson.getId())
                                );

                                LessonSimpleVO vo = new LessonSimpleVO();
                                vo.setId(lesson.getId());
                                vo.setTitle(lesson.getTitle());
                                vo.setItemType(lesson.getItemType());
                                vo.setIsRequired(lesson.getIsRequired());
                                // 默认状态为UNLOCKED，如果用户有进度记录则使用实际状态
                                vo.setStatus(progress != null ? progress.getStatus() : LessonProgressStatus.UNLOCKED.name());
                                return vo;
                            })
                            .collect(Collectors.toList());

                    ChapterLessonsVO vo = new ChapterLessonsVO();
                    vo.setId(chapter.getId());
                    vo.setTitle(chapter.getTitle());
                    vo.setSortOrder(chapter.getSortOrder());
                    vo.setLockStatus(chapter.getLockStatus());
                    vo.setLessons(lessonVOs);
                    return vo;
                })
                .collect(Collectors.toList());

        CourseDetailVO vo = new CourseDetailVO();
        vo.setId(course.getId());
        vo.setProductId(course.getProductId());
        vo.setTitle(course.getTitle());
        vo.setDescription(course.getDescription());
        vo.setStatus(course.getStatus());
        vo.setProgressPercent(userCourse.getProgressPercent());
        vo.setAccessStatus(userCourse.getAccessStatus());
        vo.setChapters(chapterLessonsList);

        return vo;
    }

    @Override
    public LessonDetailVO getLessonDetail(Long userId, Long lessonId) {
        Lesson lesson = lessonMapper.selectById(lessonId);
        if (lesson == null) {
            throw new BusinessException(ResultCode.LESSON_NOT_FOUND, "课时不存在");
        }

        // Verify user has access to the course
        Chapter chapter = chapterMapper.selectById(lesson.getChapterId());
        UserCourse userCourse = userCourseMapper.selectOne(
                new LambdaQueryWrapper<UserCourse>()
                        .eq(UserCourse::getUserId, userId)
                        .eq(UserCourse::getCourseId, chapter.getCourseId())
        );

        if (userCourse == null || !AccessStatus.ACTIVE.name().equals(userCourse.getAccessStatus())) {
            throw new BusinessException(ResultCode.COURSE_NOT_ACTIVATED, "课程未激活或已过期");
        }

        // Check if chapter is locked
        if (ChapterLockStatus.LOCK.name().equals(chapter.getLockStatus())) {
            throw new BusinessException(ResultCode.LESSON_NOT_ACCESSIBLE, "章节已锁定，无法访问课时");
        }

        UserLessonProgress progress = userLessonProgressMapper.selectOne(
                new LambdaQueryWrapper<UserLessonProgress>()
                        .eq(UserLessonProgress::getUserId, userId)
                        .eq(UserLessonProgress::getLessonId, lessonId)
        );

        // Get content payload and generate presigned URLs
        Map<String, Object> contentPayload = parseJsonPayload(lesson.getContentPayload());
        contentPayload = generatePresignedUrls(contentPayload);

        // Generate assetsMapping and embed it in contentPayload
        Map<String, String> assetsMapping = generateAssetMapping(contentPayload);
        if (!assetsMapping.isEmpty()) {
            contentPayload.put("assetsMapping", assetsMapping);
        }

        LessonDetailVO vo = new LessonDetailVO();
        vo.setId(lesson.getId());
        vo.setChapterId(lesson.getChapterId());
        vo.setChapterTitle(chapter.getTitle());
        vo.setTitle(lesson.getTitle());
        vo.setItemType(lesson.getItemType());
        vo.setIsRequired(lesson.getIsRequired());
        vo.setContentPayload(contentPayload);
        vo.setSecurityConfig(buildSecurityConfig(lesson.getItemType()));
        vo.setStatus(progress != null ? progress.getStatus() : LessonProgressStatus.UNLOCKED.name());
        vo.setProgressPayload(progress != null ? parseJsonPayload(progress.getProgressPayload()) : new HashMap<String, Object>());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLessonProgress(Long userId, Long lessonId, LessonProgressUpdateRequest request) {
        Lesson lesson = lessonMapper.selectById(lessonId);
        if (lesson == null) {
            throw new BusinessException(ResultCode.LESSON_NOT_FOUND, "课时不存在");
        }

        // Skip progress update for assignments
        if (LessonItemType.ASSIGNMENT.name().equals(lesson.getItemType())) {
            throw new BusinessException(ResultCode.LESSON_IS_ASSIGNMENT_TYPE, "作业类型课时请使用提交作业接口");
        }

        UserLessonProgress progress = userLessonProgressMapper.selectOne(
                new LambdaQueryWrapper<UserLessonProgress>()
                        .eq(UserLessonProgress::getUserId, userId)
                        .eq(UserLessonProgress::getLessonId, lessonId)
        );

        String progressPayloadJson = stringifyPayload(request.getProgressPayload());

        if (progress == null) {
            // Create new progress record
            progress = new UserLessonProgress();
            progress.setUserId(userId);
            progress.setLessonId(lessonId);
            progress.setStatus(request.getStatus());
            progress.setProgressPayload(progressPayloadJson);
            progress.setLastAccessedAt(LocalDateTime.now());
            progress.setCreateTime(LocalDateTime.now());
            progress.setUpdateTime(LocalDateTime.now());
            userLessonProgressMapper.insert(progress);
        } else {
            // Update existing progress
            progress.setStatus(request.getStatus());
            progress.setProgressPayload(progressPayloadJson);
            progress.setLastAccessedAt(LocalDateTime.now());
            progress.setUpdateTime(LocalDateTime.now());
            userLessonProgressMapper.updateById(progress);
        }

        // Update overall course progress if lesson is required
        if (lesson.getIsRequired()) {
            updateCourseProgress(userId, lesson.getChapterId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitAssignment(Long userId, Long lessonId, AssignmentSubmitRequest request) {
        Lesson lesson = lessonMapper.selectById(lessonId);
        if (lesson == null) {
            throw new BusinessException(ResultCode.LESSON_NOT_FOUND, "课时不存在");
        }

        if (!LessonItemType.ASSIGNMENT.name().equals(lesson.getItemType())) {
            throw new BusinessException(ResultCode.LESSON_NOT_ASSIGNMENT_TYPE, "该课时不是作业类型");
        }

        UserAssignment assignment = userAssignmentMapper.selectOne(
                new LambdaQueryWrapper<UserAssignment>()
                        .eq(UserAssignment::getUserId, userId)
                        .eq(UserAssignment::getLessonId, lessonId)
        );

        String submissionPayloadJson = stringifyPayload(request.getSubmissionPayload());

        if (assignment == null) {
            assignment = new UserAssignment();
            assignment.setUserId(userId);
            assignment.setLessonId(lessonId);
            assignment.setStatus(AssignmentStatus.SUBMITTED.name());
            assignment.setSubmissionPayload(submissionPayloadJson);
            assignment.setSubmittedAt(LocalDateTime.now());
            assignment.setCreateTime(LocalDateTime.now());
            assignment.setUpdateTime(LocalDateTime.now());
            userAssignmentMapper.insert(assignment);
        } else {
            assignment.setStatus(AssignmentStatus.SUBMITTED.name());
            assignment.setSubmissionPayload(submissionPayloadJson);
            assignment.setSubmittedAt(LocalDateTime.now());
            assignment.setUpdateTime(LocalDateTime.now());
            userAssignmentMapper.updateById(assignment);
        }
    }

    @Override
    public PageResponse<MyCourseVO> getMyCourses(Long userId, Integer current, Integer pageSize) {
        Page<UserCourse> page = new Page<>(current, pageSize);
        userCourseMapper.selectPage(page,
                new LambdaQueryWrapper<UserCourse>()
                        .eq(UserCourse::getUserId, userId)
                        .orderByDesc(UserCourse::getActivatedAt)
        );

        List<MyCourseVO> voList = page.getRecords().stream()
                .map(userCourse -> {
                    Course course = courseMapper.selectById(userCourse.getCourseId());
                    MyCourseVO vo = new MyCourseVO();
                    vo.setId(course.getId());
                    vo.setTitle(course.getTitle());
                    vo.setDescription(course.getDescription());
                    vo.setProgressPercent(userCourse.getProgressPercent());
                    vo.setAccessStatus(userCourse.getAccessStatus());
                    vo.setActivatedAt(formatDateTime(userCourse.getActivatedAt()));
                    vo.setValidUntil(formatDateTime(userCourse.getValidUntil()));
                    return vo;
                })
                .collect(Collectors.toList());

        return PageResponse.of(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public AssignmentDetailVO getAssignmentDetail(Long userId, Long lessonId) {
        UserAssignment assignment = userAssignmentMapper.selectOne(
                new LambdaQueryWrapper<UserAssignment>()
                        .eq(UserAssignment::getUserId, userId)
                        .eq(UserAssignment::getLessonId, lessonId)
        );

        if (assignment == null) {
            throw new BusinessException(ResultCode.ASSIGNMENT_NOT_FOUND, "作业不存在");
        }

        Lesson lesson = lessonMapper.selectById(lessonId);
        if (lesson == null) {
            throw new BusinessException(ResultCode.LESSON_NOT_FOUND, "课时不存在");
        }

        AssignmentDetailVO vo = new AssignmentDetailVO();
        vo.setId(assignment.getId());
        vo.setLessonId(lessonId);
        vo.setLessonTitle(lesson.getTitle());
        vo.setStatus(assignment.getStatus());
        vo.setSubmissionPayload(parseJsonPayload(assignment.getSubmissionPayload()));
        vo.setScore(assignment.getScore());
        vo.setFeedback(assignment.getFeedback());
        vo.setSubmittedAt(formatDateTime(assignment.getSubmittedAt()));
        vo.setGradedAt(formatDateTime(assignment.getGradedAt()));

        return vo;
    }

    // ==================== Admin API ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCourse(AdminCourseSaveRequest request) {
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setStatus(request.getStatus());
        course.setProductId(request.getProductId());
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());

        if (request.getId() != null) {
            course.setId(request.getId());
            courseMapper.updateById(course);
        } else {
            courseMapper.insert(course);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveChapter(AdminChapterSaveRequest request) {
        Chapter chapter = new Chapter();
        chapter.setCourseId(request.getCourseId());
        chapter.setTitle(request.getTitle());
        chapter.setSortOrder(request.getSortOrder());
        chapter.setLockStatus(request.getLockStatus() != null ? request.getLockStatus() : ChapterLockStatus.UNLOCK.name());
        chapter.setCreateTime(LocalDateTime.now());
        chapter.setUpdateTime(LocalDateTime.now());

        if (request.getId() != null) {
            chapter.setId(request.getId());
            chapterMapper.updateById(chapter);
        } else {
            chapterMapper.insert(chapter);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLesson(AdminLessonSaveRequest request) {
        // Validate item type
        try {
            LessonItemType.valueOf(request.getItemType());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.LESSON_TYPE_INVALID, "无效的课时类型");
        }

        // Validate content payload based on item type
        validateContentPayload(request.getItemType(), request.getContentPayload());

        Lesson lesson = new Lesson();
        lesson.setChapterId(request.getChapterId());
        lesson.setTitle(request.getTitle());
        lesson.setItemType(request.getItemType());
        lesson.setIsRequired(request.getIsRequired() != null ? request.getIsRequired() : true);
        lesson.setSortOrder(request.getSortOrder());
        lesson.setContentPayload(stringifyPayload(request.getContentPayload()));
        lesson.setCreateTime(LocalDateTime.now());
        lesson.setUpdateTime(LocalDateTime.now());

        if (request.getId() != null) {
            lesson.setId(request.getId());
            lessonMapper.updateById(lesson);
        } else {
            lessonMapper.insert(lesson);
        }
    }

    @Override
    public PageResponse<AdminAssignmentVO> getAssignments(AdminAssignmentListRequest request) {
        Page<UserAssignment> page = new Page<>(request.getCurrent(), request.getPageSize());

        LambdaQueryWrapper<UserAssignment> wrapper = new LambdaQueryWrapper<>();

        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            wrapper.eq(UserAssignment::getStatus, request.getStatus());
        }

        if (request.getLessonId() != null) {
            wrapper.eq(UserAssignment::getLessonId, request.getLessonId());
        }

        if (request.getCourseId() != null) {
            List<Lesson> lessons = lessonMapper.selectList(
                    new LambdaQueryWrapper<Lesson>()
                            .inSql(Lesson::getId,
                                    "SELECT id FROM t_lesson WHERE chapter_id IN (SELECT id FROM t_chapter WHERE course_id = " + request.getCourseId() + ")")
            );
            if (!lessons.isEmpty()) {
                List<Long> lessonIds = lessons.stream().map(Lesson::getId).collect(Collectors.toList());
                wrapper.in(UserAssignment::getLessonId, lessonIds);
            }
        }

        wrapper.orderByDesc(UserAssignment::getSubmittedAt);
        userAssignmentMapper.selectPage(page, wrapper);

        List<AdminAssignmentVO> voList = page.getRecords().stream()
                .map(assignment -> {
                    Lesson lesson = lessonMapper.selectById(assignment.getLessonId());
                    Chapter chapter = chapterMapper.selectById(lesson.getChapterId());
                    Course course = courseMapper.selectById(chapter.getCourseId());
                    User user = userMapper.selectById(assignment.getUserId());

                    AdminAssignmentVO vo = new AdminAssignmentVO();
                    vo.setId(assignment.getId());
                    vo.setUserId(assignment.getUserId());
                    vo.setUsername(user != null ? user.getUsername() : "未知用户");
                    vo.setLessonId(assignment.getLessonId());
                    vo.setLessonTitle(lesson.getTitle());
                    vo.setCourseId(course.getId());
                    vo.setCourseTitle(course.getTitle());
                    vo.setStatus(assignment.getStatus());
                    vo.setScore(assignment.getScore());
                    vo.setSubmittedAt(formatDateTime(assignment.getSubmittedAt()));
                    return vo;
                })
                .collect(Collectors.toList());

        return PageResponse.of(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void gradeAssignment(Long assignmentId, Long graderId, AdminAssignmentGradeRequest request) {
        UserAssignment assignment = userAssignmentMapper.selectById(assignmentId);
        if (assignment == null) {
            throw new BusinessException(ResultCode.ASSIGNMENT_NOT_FOUND, "作业不存在");
        }

        if (request.getRejected() != null && request.getRejected()) {
            assignment.setStatus(AssignmentStatus.REJECTED.name());
        } else {
            assignment.setStatus(AssignmentStatus.GRADED.name());
            assignment.setScore(request.getScore());
        }
        assignment.setFeedback(request.getFeedback());
        assignment.setGradedAt(LocalDateTime.now());
        assignment.setGraderId(graderId);
        assignment.setUpdateTime(LocalDateTime.now());

        userAssignmentMapper.updateById(assignment);

        // Update lesson progress to completed if approved
        if (!request.getRejected() || AssignmentStatus.GRADED.name().equals(assignment.getStatus())) {
            UserLessonProgress progress = userLessonProgressMapper.selectOne(
                    new LambdaQueryWrapper<UserLessonProgress>()
                            .eq(UserLessonProgress::getUserId, assignment.getUserId())
                            .eq(UserLessonProgress::getLessonId, assignment.getLessonId())
            );

            if (progress != null) {
                progress.setStatus(LessonProgressStatus.COMPLETED.name());
                progress.setUpdateTime(LocalDateTime.now());
                userLessonProgressMapper.updateById(progress);
            }
        }
    }

    @Override
    public AdminUserCourseVO getUserCourses(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户不存在");
        }

        List<UserCourse> userCourses = userCourseMapper.selectList(
                new LambdaQueryWrapper<UserCourse>()
                        .eq(UserCourse::getUserId, userId)
        );

        List<CourseProgressVO> courseProgressList = userCourses.stream()
                .map(userCourse -> {
                    Course course = courseMapper.selectById(userCourse.getCourseId());
                    CourseProgressVO vo = new CourseProgressVO();
                    vo.setCourseId(course.getId());
                    vo.setCourseTitle(course.getTitle());
                    vo.setAccessStatus(userCourse.getAccessStatus());
                    vo.setProgressPercent(userCourse.getProgressPercent());
                    vo.setActivatedAt(formatDateTime(userCourse.getActivatedAt()));
                    vo.setValidUntil(formatDateTime(userCourse.getValidUntil()));
                    return vo;
                })
                .collect(Collectors.toList());

        AdminUserCourseVO vo = new AdminUserCourseVO();
        vo.setUserId(userId);
        vo.setUsername(user.getUsername());
        vo.setCourses(courseProgressList);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserCourseAccess(Long userId, Long courseId, AdminUserCourseAccessRequest request) {
        UserCourse userCourse = userCourseMapper.selectOne(
                new LambdaQueryWrapper<UserCourse>()
                        .eq(UserCourse::getUserId, userId)
                        .eq(UserCourse::getCourseId, courseId)
        );

        if (userCourse == null) {
            throw new BusinessException(ResultCode.COURSE_ACTIVATION_RECORD_NOT_FOUND, "课程激活记录不存在");
        }

        if (request.getAccessStatus() != null) {
            userCourse.setAccessStatus(request.getAccessStatus());
        }
        if (request.getValidUntil() != null) {
            userCourse.setValidUntil(request.getValidUntil());
        }
        userCourse.setUpdateTime(LocalDateTime.now());
        userCourseMapper.updateById(userCourse);
    }

    @Override
    public AdminLessonVO getLesson(Long id) {
        Lesson lesson = lessonMapper.selectById(id);
        if (lesson == null) {
            throw new BusinessException(ResultCode.LESSON_NOT_FOUND, "课时不存在");
        }

        AdminLessonVO vo = new AdminLessonVO();
        vo.setId(lesson.getId());
        vo.setChapterId(lesson.getChapterId());
        vo.setTitle(lesson.getTitle());
        vo.setItemType(lesson.getItemType());
        vo.setIsRequired(lesson.getIsRequired());
        vo.setSortOrder(lesson.getSortOrder());
        Map<String, Object> contentPayload = parseJsonPayload(lesson.getContentPayload());
        vo.setContentPayload(contentPayload);
        // Generate assetMapping and embed it in contentPayload
Map<String, String> assetMapping = generateAssetMapping(contentPayload);
if (!assetMapping.isEmpty()) {
contentPayload.put("assetsMapping", assetMapping);
}

        return vo;
    }

    // ==================== Helper Methods ====================

    private void updateCourseProgress(Long userId, Long chapterId) {
        List<Lesson> lessons = lessonMapper.selectList(
                new LambdaQueryWrapper<Lesson>()
                        .eq(Lesson::getChapterId, chapterId)
                        .eq(Lesson::getIsRequired, true)
        );

        if (lessons.isEmpty()) {
            return;
        }

        long completedCount = lessons.stream()
                .filter(lesson -> {
                    UserLessonProgress progress = userLessonProgressMapper.selectOne(
                            new LambdaQueryWrapper<UserLessonProgress>()
                                    .eq(UserLessonProgress::getUserId, userId)
                                    .eq(UserLessonProgress::getLessonId, lesson.getId())
                    );
                    return progress != null && LessonProgressStatus.COMPLETED.name().equals(progress.getStatus());
                })
                .count();

        int progressPercent = (int) ((completedCount * 100) / lessons.size());

        Chapter chapter = chapterMapper.selectById(chapterId);
        UserCourse userCourse = userCourseMapper.selectOne(
                new LambdaQueryWrapper<UserCourse>()
                        .eq(UserCourse::getUserId, userId)
                        .eq(UserCourse::getCourseId, chapter.getCourseId())
        );

        if (userCourse != null) {
            userCourse.setProgressPercent(progressPercent);
            userCourse.setUpdateTime(LocalDateTime.now());
            userCourseMapper.updateById(userCourse);
        }
    }

    private Map<String, Object> parseJsonPayload(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<String, Object>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<String, Object>();
        }
    }

    private String stringifyPayload(Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> generatePresignedUrls(Map<String, Object> contentPayload) {
        Map<String, Object> result = new HashMap<String, Object>(contentPayload);

        // 根据 fileId 获取带签名的 URL
        if (result.containsKey("fileId")) {
            String fileId = (String) result.get("fileId");
            try {
                FileQueryRequest request = new FileQueryRequest();
                request.setFileUuid(fileId);
                FileVO fileVO = fileService.getFile(request);
                result.put("signedUrl", fileVO.getSignedUrl());
                result.put("fileName", fileVO.getFileName());
                result.put("contentType", fileVO.getContentType());
            } catch (Exception e) {
                // 如果文件不存在，保留原始 fileId
            }
        }

        return result;
    }

    private Map<String, Object> buildSecurityConfig(String itemType) {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("disableCopy", true);
        config.put("disableDownload", true);
        config.put("watermark", true);

        if (LessonItemType.VIDEO.name().equals(itemType)) {
            config.put("disableSeek", false); // Video type may allow seeking
        } else {
            config.put("disableSeek", true);
        }

        return config;
    }

    private void validateContentPayload(String itemType, Map<String, Object> contentPayload) {
        if (contentPayload == null || contentPayload.isEmpty()) {
            throw new BusinessException(ResultCode.LESSON_CONTENT_EMPTY, "内容数据不能为空");
        }

        LessonItemType type = LessonItemType.valueOf(itemType);

        switch (type) {
            case VIDEO:
                if (!contentPayload.containsKey("fileUrl")) {
                    throw new BusinessException(ResultCode.LESSON_CONTENT_MISSING_REQUIRED_FIELD, "视频课时必须包含fileUrl");
                }
                break;
            case DOCUMENT:
                if (!contentPayload.containsKey("fileUrl")) {
                    throw new BusinessException(ResultCode.LESSON_CONTENT_MISSING_REQUIRED_FIELD, "文档课时必须包含fileUrl");
                }
                break;
            case PODCAST:
                if (!contentPayload.containsKey("fileUrl")) {
                    throw new BusinessException(ResultCode.LESSON_CONTENT_MISSING_REQUIRED_FIELD, "播客课时必须包含fileUrl");
                }
                break;
            case ASSIGNMENT:
                if (!contentPayload.containsKey("description")) {
                    throw new BusinessException(ResultCode.LESSON_CONTENT_MISSING_REQUIRED_FIELD, "作业课时必须包含description");
                }
                break;
            case INTERACTIVE:
                if (!contentPayload.containsKey("content")) {
                    throw new BusinessException(ResultCode.LESSON_CONTENT_MISSING_REQUIRED_FIELD, "交互课时必须包含content");
                }
                break;
        }
    }

    private Map<String, String> generateAssetMapping(Map<String, Object> contentPayload) {
 Map<String, String> assetMapping = new HashMap<>();

 // Check if contentPayload contains "assets" array
 if (contentPayload != null && contentPayload.containsKey("assets")) {
 Object assetsObj = contentPayload.get("assets");
 if (assetsObj instanceof List) {
 List<?> assets = (List<?>) assetsObj;
 for (Object asset : assets) {
 if (asset instanceof String) {
 String fileUuid = (String) asset;
 try {
 // Look up file by fileUuid
 FileQueryRequest request = new FileQueryRequest();
 request.setFileUuid(fileUuid);
 FileVO fileVO = fileService.getFile(request);
 // Put fileUuid -> signedUrl mapping
 assetMapping.put(fileUuid, fileVO.getSignedUrl());
 } catch (Exception e) {
 // If file not found or error, skip this asset
 log.warn("Failed to generate signed URL for fileUuid: {}", fileUuid);
 }
 }
 }
 }
 }

 return assetMapping;
 }

 private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toString();
    }
}
