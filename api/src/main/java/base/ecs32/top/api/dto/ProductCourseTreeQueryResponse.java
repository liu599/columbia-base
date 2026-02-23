package base.ecs32.top.api.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 产品课程树形结构查询响应
 */
@Data
public class ProductCourseTreeQueryResponse {

    /**
     * 产品 ID
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品描述
     */
    private String productDescription;

    /**
     * 产品基础学分
     */
    private Integer productBaseCredits;

    /**
     * 产品状态
     */
    private Integer productStatus;

    /**
     * 课程列表（当查询产品下所有课程时）
     */
    private List<CourseTree> courses;

    /**
     * 单个课程树（当查询特定课程时）
     */
    private CourseTree course;

    /**
     * 内容层级
     */
    private String treeLevel;

    /**
     * 课程树结构
     */
    @Data
    public static class CourseTree {
        // 课程基础信息
        private Long id;
        private String title;
        private String description;
        private String status;
        private Long productId;

        // 仅 FULL 层级返回
        private Map<String, Object> contentPayload;

        // 章节列表
        private List<ChapterTree> chapters;
    }

    /**
     * 章节树结构
     */
    @Data
    public static class ChapterTree {
        // 章节基础信息
        private Long id;
        private String title;
        private Integer sortOrder;
        private Long courseId;

        // 课时列表
        private List<LessonTree> lessons;
    }

    /**
     * 课时树结构
     */
    @Data
    public static class LessonTree {
        // 课时基础信息
        private Long id;
        private String title;
        private String itemType;
        private Boolean isRequired;
        private Integer sortOrder;
        private Long chapterId;

        // 仅 FULL 层级返回
        private Map<String, Object> contentPayload;
    }
}
