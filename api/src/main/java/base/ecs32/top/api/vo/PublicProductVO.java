package base.ecs32.top.api.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 公开产品信息 VO
 */
@Data
public class PublicProductVO {
    private Long id;
    private String name;
    private String description;
    private Integer baseCredits;
    private Integer status;
    private String coverUrl; // 封面图临时链接
    private List<CourseInfo> courses = new ArrayList<>();

    @Data
    public static class CourseInfo {
        private Long id;
        private String title;
        private String description;
        private String status;
        private Long productId;
        private String coverUrl; // 封面图临时链接
        private List<ChapterInfo> chapters = new ArrayList<>();
    }

    @Data
    public static class ChapterInfo {
        private Long id;
        private String title;
        private Integer sortOrder;
        private Long courseId;
        /**
         * 章节下的 lesson 总数量
         */
        private Integer lessonCount;
    }
}
