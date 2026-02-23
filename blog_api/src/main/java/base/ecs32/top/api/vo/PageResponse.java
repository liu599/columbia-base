package base.ecs32.top.api.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> list;
    private Long total;
    private Integer current;
    private Integer pageSize;
    private Integer pages;

    public static <T> PageResponse<T> of(List<T> list, Long total, Integer current, Integer pageSize) {
        PageResponse<T> response = new PageResponse<>();
        response.setList(list);
        response.setTotal(total);
        response.setCurrent(current);
        response.setPageSize(pageSize);
        response.setPages((int) Math.ceil((double) total / pageSize));
        return response;
    }
}
