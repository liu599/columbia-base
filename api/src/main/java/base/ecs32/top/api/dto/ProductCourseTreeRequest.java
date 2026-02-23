package base.ecs32.top.api.dto;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 产品课程树形结构请求
 * 用于创建或更新产品下的完整课程结构
 */
@Data
public class ProductCourseTreeRequest {

    /**
     * 产品ID
     * 不传则创建新产品，传入则更新现有产品
     */
    private Long productId;

    /**
     * 产品名称
     * 创建时必填，更新时可选
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
     * 课程列表
     */
    @Valid
    @NotEmpty(message = "课程列表不能为空")
    private List<CourseData> courses;

    /**
     * 课程数据
     */
    @Data
    public static class CourseData {

        /**
         * 课程ID
         * 不传则创建新课程，传入则更新现有课程
         */
        private Long courseId;

        /**
         * 课程标题
         */
        @NotBlank(message = "课程标题不能为空")
        private String title;

        /**
         * 课程描述
         */
        private String description;

        /**
         * 课程状态
         */
        private String status;

        /**
         * 章节列表
         */
        @Valid
        @NotEmpty(message = "课程至少需要一个章节")
        private List<ChapterData> chapters;
    }

    /**
     * 章节数据
     */
    @Data
    public static class ChapterData {

        /**
         * 章节ID
         * 不传则创建新章节，传入则更新现有章节
         */
        private Long chapterId;

        /**
         * 章节标题
         */
        @NotBlank(message = "章节标题不能为空")
        private String title;

        /**
         * 排序序号
         */
        private Integer sortOrder;

        /**
         * 课时列表
         */
        @Valid
        private List<LessonData> lessons;
    }

    /**
     * 课时数据
     */
    @Data
    public static class LessonData {

        /**
         * 课时ID
         * 不传则创建新课时，传入则更新现有课时
         */
        private Long lessonId;

        /**
         * 课时标题
         */
        @NotBlank(message = "课时标题不能为空")
        private String title;

        /**
         * 排序序号
         */
        private Integer sortOrder;

        /**
         * 课时类型
         * 创建时可传（后续可修改），更新时可覆盖
         */
        private String itemType;

        /**
         * 是否必选
         */
        private Boolean isRequired;

        /**
         * 内容负载
         * JSON格式，根据itemType包含不同字段
         */
        private String contentPayload;
    }
}
