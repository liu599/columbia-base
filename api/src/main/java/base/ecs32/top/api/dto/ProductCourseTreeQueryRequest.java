package base.ecs32.top.api.dto;

import lombok.Data;

/**
 * 产品课程树形结构查询请求
 */
@Data
public class ProductCourseTreeQueryRequest {

    /**
     * 产品 ID
     */
    private Long productId;

    /**
     * 课程 ID（可选，查询特定课程的完整树）
     * 不传则返回产品下所有课程的列表
     */
    private Long courseId;

    /**
     * 返回内容层级
     * TITLES_ONLY - 仅返回标题树（id, title, sortOrder）
     * BASIC - 返回基础信息（id, title, description, status, sortOrder 等）
     * FULL - 返回完整内容树（包含 contentPayload 等所有字段）
     */
    private TreeLevel treeLevel = TreeLevel.BASIC;

    public enum TreeLevel {
        /**
         * 仅标题：适合树形结构展示、快速加载
         */
        TITLES_ONLY,

        /**
         * 基础信息：适合列表展示、概览查看
         */
        BASIC,

        /**
         * 完整内容：适合编辑、详情查看
         */
        FULL
    }
}
