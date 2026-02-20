package base.ecs32.top.api.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserActivationsVO {
    private Long userId;
    private Integer totalActivations;
    private List<UserActivationVO> activations;
}
