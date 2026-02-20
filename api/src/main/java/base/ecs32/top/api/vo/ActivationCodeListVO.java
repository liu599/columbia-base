package base.ecs32.top.api.vo;

import lombok.Data;
import base.ecs32.top.enums.ActivationCodeStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivationCodeListVO {
    private Long id;
    private String code;
    private Long productId;
    private String productName;
    private String productDescription;
    private ActivationCodeStatus status;
    private String statusDescription;
    private Long userId;
    private String username;
    private Integer userRoleLevel;
    private String userStatus;
    private LocalDateTime usedTime;
    private LocalDateTime createTime;

    @Data
    public static class ActivationCodePageResponse {
        private List<ActivationCodeListVO> list;
        private Long total;
        private Integer current;
        private Integer pageSize;
        private Integer pages;

        public static ActivationCodePageResponse of(List<ActivationCodeListVO> list, Long total, Integer current, Integer pageSize) {
            ActivationCodePageResponse response = new ActivationCodePageResponse();
            response.setList(list);
            response.setTotal(total);
            response.setCurrent(current);
            response.setPageSize(pageSize);
            response.setPages((int) Math.ceil((double) total / pageSize));
            return response;
        }
    }
}
