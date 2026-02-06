package base.ecs32.top.api.util;

import base.ecs32.top.api.dto.SearchRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public class QueryWrapperUtils {

    public static <T> QueryWrapper<T> buildWrapper(SearchRequest request, List<String> keywordFields) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();

        // 1. Keyword search (OR LIKE)
        if (StringUtils.hasText(request.getKeyword()) && !CollectionUtils.isEmpty(keywordFields)) {
            wrapper.and(w -> {
                for (int i = 0; i < keywordFields.size(); i++) {
                    w.like(keywordFields.get(i), request.getKeyword());
                    if (i < keywordFields.size() - 1) {
                        w.or();
                    }
                }
            });
        }

        // 2. Exact Filters (EQ)
        if (!CollectionUtils.isEmpty(request.getFilter())) {
            for (Map.Entry<String, Object> entry : request.getFilter().entrySet()) {
                if (entry.getValue() != null) {
                    wrapper.eq(entry.getKey(), entry.getValue());
                }
            }
        }

        // 3. Range search (GE/LE)
        if (!CollectionUtils.isEmpty(request.getRange())) {
            for (Map.Entry<String, List<Object>> entry : request.getRange().entrySet()) {
                List<Object> values = entry.getValue();
                if (!CollectionUtils.isEmpty(values)) {
                    if (values.size() >= 1 && values.get(0) != null) {
                        wrapper.ge(entry.getKey(), values.get(0));
                    }
                    if (values.size() >= 2 && values.get(1) != null) {
                        wrapper.le(entry.getKey(), values.get(1));
                    }
                }
            }
        }

        // 4. Sorting
        if (request.getSort() != null && StringUtils.hasText(request.getSort().getField())) {
            boolean isAsc = "asc".equalsIgnoreCase(request.getSort().getOrder());
            wrapper.orderBy(true, isAsc, request.getSort().getField());
        }

        return wrapper;
    }
}
