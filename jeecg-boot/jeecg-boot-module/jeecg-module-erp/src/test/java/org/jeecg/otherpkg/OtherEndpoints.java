package org.jeecg.otherpkg;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.business.order.entity.JshOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 非 ERP 包的 controller，用于确认 Long→String 不影响上游接口。 */
@RestController
public class OtherEndpoints {
    @GetMapping("/other/entity")
    public Result<?> entity() {
        JshOrder order = new JshOrder();
        order.setId(1000000270140010002L);
        return Result.OK(order);
    }
}
