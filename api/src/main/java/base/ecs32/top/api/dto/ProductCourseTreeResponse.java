package base.ecs32.top.api.dto;

import lombok.Data;
import java.util.List;

/**
 * 产品课程树形结构响应
 * 返回创建/更新后的完整树形结构
 */
@Data
public class ProductCourseTreeResponse {

    /**
     * 产品ID
     */
    private Long productId;

    /**
     * 产品信息
     */
    private ProductInfo product;

    /**
     * 课程列表
     */
    private List<CourseData> courses;

    /**
     * 操作类型
     * CREATE 或 UPDATE
     */
    private String operationType;

    /**
     * 统计信息
     */
    private OperationStats stats;

    @Data
    public static class ProductInfo {
        private Long id;
        private String name;
        private String description;
        private Integer baseCredits;
        private Integer status;
        private Long cover; // 封面图文件 ID
    }

    @Data
    public static class CourseData {
        private Long id;
        private String title;
        private String description;
        private String status;
        private Long productId;
        private Long cover; // 封面图文件 ID
        private List<ChapterData> chapters;
    }

    @Data
    public static class ChapterData {
        private Long id;
        private String title;
        private Integer sortOrder;
        private Long courseId;
        private List<LessonData> lessons;
    }

    @Data
    public static class LessonData {
        private Long id;
        private String title;
        private String itemType;
        private Boolean isRequired;
        private Integer sortOrder;
        private Long chapterId;
    }

    @Data
    public static class OperationStats {
        // 产品操作统计
        private boolean productCreated;
        private boolean productUpdated;

        // 课程操作统计
        private int coursesCreated;
        private int coursesUpdated;

        // 章节操作统计
        private int chaptersCreated;
        private int chaptersUpdated;
        private int chaptersDeleted;

        // 课时操作统计
        private int lessonsCreated;
        private int lessonsUpdated;
        private int lessonsDeleted;
    }
}
