package base.ecs32.top.api.service.impl;

import base.ecs32.top.api.advice.BusinessException;
import base.ecs32.top.api.advice.ResultCode;
import base.ecs32.top.api.dto.ProductCourseTreeQueryRequest;
import base.ecs32.top.api.dto.ProductCourseTreeQueryResponse;
import base.ecs32.top.api.dto.ProductCourseTreeRequest;
import base.ecs32.top.api.dto.ProductCourseTreeResponse;
import base.ecs32.top.api.dto.SearchRequest;
import base.ecs32.top.api.service.CourseService;
import base.ecs32.top.api.service.ProductService;
import base.ecs32.top.api.util.QueryWrapperUtils;
import base.ecs32.top.api.vo.PageResponse;
import base.ecs32.top.dao.ChapterMapper;
import base.ecs32.top.dao.CourseMapper;
import base.ecs32.top.dao.LessonMapper;
import base.ecs32.top.dao.ProductMapper;
import base.ecs32.top.entity.Chapter;
import base.ecs32.top.entity.Course;
import base.ecs32.top.entity.Lesson;
import base.ecs32.top.entity.Product;
import base.ecs32.top.enums.LessonItemType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;
    private final CourseMapper courseMapper;
    private final ChapterMapper chapterMapper;
    private final LessonMapper lessonMapper;
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<Product> listProducts(SearchRequest request) {
        Page<Product> page = new Page<>(request.getCurrent(), request.getPageSize());
        QueryWrapper<Product> wrapper = QueryWrapperUtils.buildWrapper(request, Arrays.asList("name", "description"));
        productMapper.selectPage(page, wrapper);
        return PageResponse.of(page.getRecords(), page.getTotal(), (int)page.getCurrent(), (int)page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductCourseTreeResponse saveProductCourseTree(ProductCourseTreeRequest request) {
        ProductCourseTreeResponse response = new ProductCourseTreeResponse();
        ProductCourseTreeResponse.OperationStats stats = new ProductCourseTreeResponse.OperationStats();
        response.setStats(stats);

        // 1. 处理产品层
        Product product = handleProduct(request, stats);
        response.setProductId(product.getId());
        response.setProduct(buildProductInfo(product));

        // 2. 处理课程层（现在是数组）
        List<ProductCourseTreeResponse.CourseData> courseDataList = handleCourses(request.getCourses(), product.getId(), stats);
        response.setCourses(courseDataList);

        // 3. 设置操作类型
        boolean isUpdate = request.getProductId() != null;
        // 检查是否有任何课程在更新状态
        for (ProductCourseTreeRequest.CourseData courseData : request.getCourses()) {
            if (courseData.getCourseId() != null) {
                isUpdate = true;
                break;
            }
        }
        response.setOperationType(isUpdate ? "UPDATE" : "CREATE");

        return response;
    }

    private Product handleProduct(ProductCourseTreeRequest request, ProductCourseTreeResponse.OperationStats stats) {
        Product product;

        if (request.getProductId() != null) {
            // 更新模式
            product = productMapper.selectById(request.getProductId());
            if (product == null) {
                throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND, "产品不存在");
            }
            stats.setProductUpdated(true);

            // 只更新提供的字段
            if (request.getProductName() != null) {
                product.setName(request.getProductName());
            }
            if (request.getProductDescription() != null) {
                product.setDescription(request.getProductDescription());
            }
            if (request.getProductBaseCredits() != null) {
                product.setBaseCredits(request.getProductBaseCredits());
            }
            if (request.getProductStatus() != null) {
                product.setStatus(request.getProductStatus());
            }
            productMapper.updateById(product);
        } else {
            // 创建模式
            product = new Product();
            product.setName(request.getProductName());
            product.setDescription(request.getProductDescription());
            product.setBaseCredits(request.getProductBaseCredits());
            product.setStatus(request.getProductStatus() != null ? request.getProductStatus() : 1);
            productMapper.insert(product);
            stats.setProductCreated(true);
        }

        return product;
    }

    private List<ProductCourseTreeResponse.CourseData> handleCourses(List<ProductCourseTreeRequest.CourseData> requestCourses,
                                                                    Long productId,
                                                                    ProductCourseTreeResponse.OperationStats stats) {
        List<ProductCourseTreeResponse.CourseData> courseDataList = new ArrayList<>();

        for (ProductCourseTreeRequest.CourseData requestCourse : requestCourses) {
            Course course = handleSingleCourse(requestCourse, productId, stats);
            ProductCourseTreeResponse.CourseData courseData = buildCourseData(course);

            // 处理章节和课时层
            handleChaptersAndLessons(requestCourse, course.getId(), courseData, stats);

            // 清理旧数据（更新模式下）
            if (requestCourse.getCourseId() != null) {
                cleanupOldEntities(requestCourse.getCourseId(), requestCourse.getChapters(), stats);
            }

            courseDataList.add(courseData);
        }

        return courseDataList;
    }

    private Course handleSingleCourse(ProductCourseTreeRequest.CourseData requestCourse, Long productId,
                                     ProductCourseTreeResponse.OperationStats stats) {
        Course course;

        if (requestCourse.getCourseId() != null) {
            // 更新模式
            course = courseMapper.selectById(requestCourse.getCourseId());
            if (course == null) {
                throw new BusinessException(ResultCode.COURSE_NOT_FOUND, "课程不存在");
            }
            stats.setCoursesUpdated(stats.getCoursesUpdated() + 1);

            // 更新字段
            course.setTitle(requestCourse.getTitle());
            if (requestCourse.getDescription() != null) {
                course.setDescription(requestCourse.getDescription());
            }
            if (requestCourse.getStatus() != null) {
                course.setStatus(requestCourse.getStatus());
            }
            course.setProductId(productId);
            course.setUpdateTime(LocalDateTime.now());
            courseMapper.updateById(course);
        } else {
            // 创建模式
            course = new Course();
            course.setTitle(requestCourse.getTitle());
            course.setDescription(requestCourse.getDescription());
            course.setStatus(requestCourse.getStatus() != null ? requestCourse.getStatus() : "DRAFT");
            course.setProductId(productId);
            course.setCreateTime(LocalDateTime.now());
            course.setUpdateTime(LocalDateTime.now());
            courseMapper.insert(course);
            stats.setCoursesCreated(stats.getCoursesCreated() + 1);
        }

        return course;
    }

    private void handleChaptersAndLessons(ProductCourseTreeRequest.CourseData requestCourse, Long courseId,
                                           ProductCourseTreeResponse.CourseData courseData,
                                           ProductCourseTreeResponse.OperationStats stats) {
        List<ProductCourseTreeRequest.ChapterData> requestChapters = requestCourse.getChapters();
        List<ProductCourseTreeResponse.ChapterData> chaptersDataList = new ArrayList<>();

        for (int i = 0; i < requestChapters.size(); i++) {
            ProductCourseTreeRequest.ChapterData requestChapter = requestChapters.get(i);
            Chapter chapter = handleChapter(requestChapter, courseId, i, stats);
            ProductCourseTreeResponse.ChapterData chapterData = buildChapterData(chapter);

            // 处理课时
            handleLessons(requestChapter, chapter.getId(), chapterData, stats);

            chaptersDataList.add(chapterData);
        }

        courseData.setChapters(chaptersDataList);
    }

    private Chapter handleChapter(ProductCourseTreeRequest.ChapterData requestChapter, Long courseId, int index,
                                   ProductCourseTreeResponse.OperationStats stats) {
        Chapter chapter;

        if (requestChapter.getChapterId() != null) {
            // 更新模式
            chapter = chapterMapper.selectById(requestChapter.getChapterId());
            if (chapter == null) {
                throw new BusinessException(ResultCode.CHAPTER_NOT_FOUND, "章节不存在，ID: " + requestChapter.getChapterId());
            }
            stats.setChaptersUpdated(stats.getChaptersUpdated() + 1);

            chapter.setTitle(requestChapter.getTitle());
            chapter.setSortOrder(requestChapter.getSortOrder() != null ? requestChapter.getSortOrder() : index);
            chapter.setUpdateTime(LocalDateTime.now());
            chapterMapper.updateById(chapter);
        } else {
            // 创建模式
            chapter = new Chapter();
            chapter.setCourseId(courseId);
            chapter.setTitle(requestChapter.getTitle());
            chapter.setSortOrder(requestChapter.getSortOrder() != null ? requestChapter.getSortOrder() : index);
            chapter.setCreateTime(LocalDateTime.now());
            chapter.setUpdateTime(LocalDateTime.now());
            chapterMapper.insert(chapter);
            stats.setChaptersCreated(stats.getChaptersCreated() + 1);
        }

        return chapter;
    }

    private void handleLessons(ProductCourseTreeRequest.ChapterData requestChapter, Long chapterId,
                               ProductCourseTreeResponse.ChapterData chapterData,
                               ProductCourseTreeResponse.OperationStats stats) {
        List<ProductCourseTreeRequest.LessonData> requestLessons = requestChapter.getLessons();
        List<ProductCourseTreeResponse.LessonData> lessonsDataList = new ArrayList<>();

        for (int i = 0; i < requestLessons.size(); i++) {
            ProductCourseTreeRequest.LessonData requestLesson = requestLessons.get(i);
            Lesson lesson = handleLesson(requestLesson, chapterId, i, stats);
            lessonsDataList.add(buildLessonData(lesson));
        }

        chapterData.setLessons(lessonsDataList);
    }

    private Lesson handleLesson(ProductCourseTreeRequest.LessonData requestLesson, Long chapterId, int index,
                                ProductCourseTreeResponse.OperationStats stats) {
        Lesson lesson;

        if (requestLesson.getLessonId() != null) {
            // 更新模式
            lesson = lessonMapper.selectById(requestLesson.getLessonId());
            if (lesson == null) {
                throw new BusinessException(ResultCode.LESSON_NOT_FOUND, "课时不存在，ID: " + requestLesson.getLessonId());
            }
            stats.setLessonsUpdated(stats.getLessonsUpdated() + 1);

            lesson.setTitle(requestLesson.getTitle());
            if (requestLesson.getItemType() != null) {
                validateLessonItemType(requestLesson.getItemType());
                lesson.setItemType(requestLesson.getItemType());
            }
            if (requestLesson.getIsRequired() != null) {
                lesson.setIsRequired(requestLesson.getIsRequired());
            }
            if (requestLesson.getSortOrder() != null) {
                lesson.setSortOrder(requestLesson.getSortOrder());
            } else {
                lesson.setSortOrder(index);
            }
            if (requestLesson.getContentPayload() != null) {
                lesson.setContentPayload(requestLesson.getContentPayload());
            }
            lesson.setUpdateTime(LocalDateTime.now());
            lessonMapper.updateById(lesson);
        } else {
            // 创建模式
            lesson = new Lesson();
            lesson.setChapterId(chapterId);
            lesson.setTitle(requestLesson.getTitle());

            // 课时类型：如果提供了则使用，否则使用默认值
            String itemType = requestLesson.getItemType();
            if (itemType != null && !itemType.isEmpty()) {
                validateLessonItemType(itemType);
            } else {
                itemType = "VIDEO"; // 默认值
            }
            lesson.setItemType(itemType);

            // 是否必选：如果提供了则使用，否则使用默认值true
            lesson.setIsRequired(requestLesson.getIsRequired() != null ? requestLesson.getIsRequired() : true);

            lesson.setSortOrder(requestLesson.getSortOrder() != null ? requestLesson.getSortOrder() : index);
            lesson.setContentPayload(requestLesson.getContentPayload());
            lesson.setCreateTime(LocalDateTime.now());
            lesson.setUpdateTime(LocalDateTime.now());
            lessonMapper.insert(lesson);
            stats.setLessonsCreated(stats.getLessonsCreated() + 1);
        }

        return lesson;
    }

    private void cleanupOldEntities(Long courseId, List<ProductCourseTreeRequest.ChapterData> requestChapters,
                                     ProductCourseTreeResponse.OperationStats stats) {
        // 获取请求中的章节ID集合
        Set<Long> requestChapterIds = requestChapters.stream()
                .map(ProductCourseTreeRequest.ChapterData::getChapterId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 如果请求中没有任何带ID的章节（全是新建），则不需要清理旧数据
        // 这样可以避免创建新章节后立即被删除的问题
        if (requestChapterIds.isEmpty()) {
            return;
        }

        // 删除未被引用的课时
        List<Long> allChapterIdsForCourse = chapterMapper.selectList(
                new LambdaQueryWrapper<Chapter>()
                        .eq(Chapter::getCourseId, courseId)
        ).stream().map(Chapter::getId).collect(Collectors.toList());

        for (Long chapterId : allChapterIdsForCourse) {
            List<Lesson> existingLessons = lessonMapper.selectList(
                    new LambdaQueryWrapper<Lesson>()
                            .eq(Lesson::getChapterId, chapterId)
            );

            Set<Long> requestLessonIdsForChapter = requestChapters.stream()
                    .filter(ch -> Objects.equals(ch.getChapterId(), chapterId))
                    .flatMap(ch -> ch.getLessons().stream())
                    .map(ProductCourseTreeRequest.LessonData::getLessonId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            for (Lesson lesson : existingLessons) {
                if (!requestLessonIdsForChapter.contains(lesson.getId())) {
                    lessonMapper.deleteById(lesson.getId());
                    stats.setLessonsDeleted(stats.getLessonsDeleted() + 1);
                }
            }
        }

        // 删除未被引用的章节
        List<Chapter> existingChapters = chapterMapper.selectList(
                new LambdaQueryWrapper<Chapter>()
                        .eq(Chapter::getCourseId, courseId)
        );

        for (Chapter chapter : existingChapters) {
            if (!requestChapterIds.contains(chapter.getId())) {
                // 先删除该章节下的所有课时
                lessonMapper.delete(
                        new LambdaQueryWrapper<Lesson>()
                                .eq(Lesson::getChapterId, chapter.getId())
                );
                // 然后删除章节
                chapterMapper.deleteById(chapter.getId());
                stats.setChaptersDeleted(stats.getChaptersDeleted() + 1);
            }
        }
    }

    private void validateLessonItemType(String itemType) {
        try {
            LessonItemType.valueOf(itemType);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.LESSON_TYPE_INVALID, "无效的课时类型: " + itemType);
        }
    }

    private ProductCourseTreeResponse.ProductInfo buildProductInfo(Product product) {
        ProductCourseTreeResponse.ProductInfo info = new ProductCourseTreeResponse.ProductInfo();
        info.setId(product.getId());
        info.setName(product.getName());
        info.setDescription(product.getDescription());
        info.setBaseCredits(product.getBaseCredits());
        info.setStatus(product.getStatus());
        return info;
    }

    private ProductCourseTreeResponse.CourseData buildCourseData(Course course) {
        ProductCourseTreeResponse.CourseData data = new ProductCourseTreeResponse.CourseData();
        data.setId(course.getId());
        data.setTitle(course.getTitle());
        data.setDescription(course.getDescription());
        data.setStatus(course.getStatus());
        data.setProductId(course.getProductId());
        return data;
    }

    private ProductCourseTreeResponse.ChapterData buildChapterData(Chapter chapter) {
        ProductCourseTreeResponse.ChapterData data = new ProductCourseTreeResponse.ChapterData();
        data.setId(chapter.getId());
        data.setTitle(chapter.getTitle());
        data.setSortOrder(chapter.getSortOrder());
        data.setCourseId(chapter.getCourseId());
        return data;
    }

    private ProductCourseTreeResponse.LessonData buildLessonData(Lesson lesson) {
        ProductCourseTreeResponse.LessonData data = new ProductCourseTreeResponse.LessonData();
        data.setId(lesson.getId());
        data.setTitle(lesson.getTitle());
        data.setItemType(lesson.getItemType());
        data.setIsRequired(lesson.getIsRequired());
        data.setSortOrder(lesson.getSortOrder());
        data.setChapterId(lesson.getChapterId());
        return data;
    }

    @Override
    public ProductCourseTreeQueryResponse getProductCourseTree(ProductCourseTreeQueryRequest request) {
        // 验证产品是否存在
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_FOUND, "产品不存在");
        }

        ProductCourseTreeQueryResponse response = new ProductCourseTreeQueryResponse();
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setProductDescription(product.getDescription());
        response.setProductBaseCredits(product.getBaseCredits());
        response.setProductStatus(product.getStatus());
        response.setTreeLevel(request.getTreeLevel().name());

        if (request.getCourseId() != null) {
            // 查询单个课程的完整树形结构，放入 courses 数组中
            Course course = courseMapper.selectById(request.getCourseId());
            if (course == null || !course.getProductId().equals(request.getProductId())) {
                throw new BusinessException(ResultCode.COURSE_NOT_FOUND, "课程不存在或不属于该产品");
            }
            List<ProductCourseTreeQueryResponse.CourseTree> courseTrees = new ArrayList<>();
            courseTrees.add(buildCourseTree(course, request.getTreeLevel()));
            response.setCourses(courseTrees);
        } else {
            // 查询产品下所有课程
            List<Course> courses = courseMapper.selectList(
                    new LambdaQueryWrapper<Course>()
                            .eq(Course::getProductId, request.getProductId())
            );
            List<ProductCourseTreeQueryResponse.CourseTree> courseTrees = courses.stream()
                    .map(course -> buildCourseTree(course, request.getTreeLevel()))
                    .collect(Collectors.toList());
            response.setCourses(courseTrees);
        }

        return response;
    }

    private ProductCourseTreeQueryResponse.CourseTree buildCourseTree(Course course, ProductCourseTreeQueryRequest.TreeLevel treeLevel) {
        ProductCourseTreeQueryResponse.CourseTree courseTree = new ProductCourseTreeQueryResponse.CourseTree();
        courseTree.setId(course.getId());
        courseTree.setTitle(course.getTitle());
        courseTree.setProductId(course.getProductId());

        // 根据层级设置字段
        if (treeLevel != ProductCourseTreeQueryRequest.TreeLevel.TITLES_ONLY) {
            courseTree.setDescription(course.getDescription());
            courseTree.setStatus(course.getStatus());
        }

        // 获取章节
        List<Chapter> chapters = chapterMapper.selectList(
                new LambdaQueryWrapper<Chapter>()
                        .eq(Chapter::getCourseId, course.getId())
                        .orderByAsc(Chapter::getSortOrder)
        );

        List<ProductCourseTreeQueryResponse.ChapterTree> chapterTrees = chapters.stream()
                .map(chapter -> buildChapterTree(chapter, treeLevel))
                .collect(Collectors.toList());
        courseTree.setChapters(chapterTrees);

        return courseTree;
    }

    private ProductCourseTreeQueryResponse.ChapterTree buildChapterTree(Chapter chapter, ProductCourseTreeQueryRequest.TreeLevel treeLevel) {
        ProductCourseTreeQueryResponse.ChapterTree chapterTree = new ProductCourseTreeQueryResponse.ChapterTree();
        chapterTree.setId(chapter.getId());
        chapterTree.setTitle(chapter.getTitle());
        chapterTree.setSortOrder(chapter.getSortOrder());
        chapterTree.setCourseId(chapter.getCourseId());

        // 获取课时
        List<Lesson> lessons = lessonMapper.selectList(
                new LambdaQueryWrapper<Lesson>()
                        .eq(Lesson::getChapterId, chapter.getId())
                        .orderByAsc(Lesson::getSortOrder)
        );

        List<ProductCourseTreeQueryResponse.LessonTree> lessonTrees = lessons.stream()
                .map(lesson -> buildLessonTree(lesson, treeLevel))
                .collect(Collectors.toList());
        chapterTree.setLessons(lessonTrees);

        return chapterTree;
    }

    private ProductCourseTreeQueryResponse.LessonTree buildLessonTree(Lesson lesson, ProductCourseTreeQueryRequest.TreeLevel treeLevel) {
        ProductCourseTreeQueryResponse.LessonTree lessonTree = new ProductCourseTreeQueryResponse.LessonTree();
        lessonTree.setId(lesson.getId());
        lessonTree.setTitle(lesson.getTitle());
        lessonTree.setChapterId(lesson.getChapterId());

        // 根据层级设置字段
        if (treeLevel != ProductCourseTreeQueryRequest.TreeLevel.TITLES_ONLY) {
            lessonTree.setItemType(lesson.getItemType());
            lessonTree.setIsRequired(lesson.getIsRequired());
            lessonTree.setSortOrder(lesson.getSortOrder());
        }

        // FULL 层级包含 contentPayload
        if (treeLevel == ProductCourseTreeQueryRequest.TreeLevel.FULL && lesson.getContentPayload() != null) {
            lessonTree.setContentPayload(parseJsonPayload(lesson.getContentPayload()));
        }

        return lessonTree;
    }

    private Map<String, Object> parseJsonPayload(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
