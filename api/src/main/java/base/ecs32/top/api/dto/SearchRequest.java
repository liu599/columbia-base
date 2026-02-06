package base.ecs32.top.api.dto;

import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
public class SearchRequest {
    private Integer current = 1;
    private Integer pageSize = 20;
    private String keyword;
    private Map<String, Object> filter;
    private Map<String, List<Object>> range;
    private SortConfig sort;

    @Data
    public static class SortConfig {
        private String field;
        private String order; // "asc" or "desc"
    }
}
