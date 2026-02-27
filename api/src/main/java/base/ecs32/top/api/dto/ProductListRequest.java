package base.ecs32.top.api.dto;

import lombok.Data;
import java.util.List;

/**
 * 公开的产品列表请求参数
 */
@Data
public class ProductListRequest {
    /**
     * 产品 ID 数组，用于过滤特定产品
     */
    private List<Long> ids;
}
