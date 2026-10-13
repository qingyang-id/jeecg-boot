package org.jeecg.modules.business.config;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.business.order.entity.JshOrder;
import org.jeecg.modules.business.order.vo.JshOrderPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * ERP 接口里 Long 类型的 id / *Id 必须序列化成字符串（19 位主键在 JS 里会丢精度），
 * 且只作用于 org.jeecg.modules.business 包下的 controller。
 */
public class ErpLongIdToStringAdviceTest {

    static final long BIG_ID = 1000000270140010002L;

    @RestController
    static class ErpEndpoints {
        @GetMapping("/erp/entity")
        public Result<?> entity() {
            JshOrder order = new JshOrder();
            order.setId(BIG_ID);
            order.setCustomerId(1489123190154809346L);
            order.setTotalPrice(12345L);
            return Result.OK(order);
        }

        /** DictAspect 会把分页 records 换成 JSONObject，Long 原样放进 Map。 */
        @GetMapping("/erp/dictPage")
        public Result<?> dictPage() {
            JSONObject rec = new JSONObject(true);
            rec.put("id", BIG_ID);
            rec.put("orderId", BIG_ID - 2);
            rec.put("num", 3L);
            Page<JSONObject> page = new Page<>(1, 10, 1);
            page.setRecords(Collections.singletonList(rec));
            return Result.OK(page);
        }

        /** 前端以字符串回传 id。 */
        @PostMapping("/erp/echo")
        public Result<?> echo(@RequestBody JshOrderPage page) {
            return Result.OK(String.valueOf(page.getId()) + "|" + page.getCustomerId());
        }
    }

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ErpEndpoints(), new org.jeecg.otherpkg.OtherEndpoints())
                .setControllerAdvice(new ErpLongIdToStringAdvice(JsonMapper.builder().build()))
                .build();
    }

    @Test
    void entityIdsAreStrings() throws Exception {
        mvc.perform(get("/erp/entity"))
                .andExpect(jsonPath("$.result.id").isString())
                .andExpect(jsonPath("$.result.id").value(String.valueOf(BIG_ID)))
                .andExpect(jsonPath("$.result.customerId").isString())
                .andExpect(jsonPath("$.result.customerId").value("1489123190154809346"))
                .andExpect(jsonPath("$.result.totalPrice").isNumber());
    }

    @Test
    void dictAspectPageIdsAreStrings() throws Exception {
        mvc.perform(get("/erp/dictPage"))
                .andExpect(jsonPath("$.result.records[0].id").isString())
                .andExpect(jsonPath("$.result.records[0].id").value(String.valueOf(BIG_ID)))
                .andExpect(jsonPath("$.result.records[0].orderId").isString())
                .andExpect(jsonPath("$.result.records[0].num").isNumber())
                .andExpect(jsonPath("$.result.total").isNumber());
    }

    @Test
    void stringIdsDeserializeToLong() throws Exception {
        mvc.perform(post("/erp/echo").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"" + BIG_ID + "\",\"customerId\":\"1489123190154809346\"}"))
                .andExpect(jsonPath("$.result").value(BIG_ID + "|1489123190154809346"));
    }

    @Test
    void controllersOutsideErpPackageUnchanged() throws Exception {
        mvc.perform(get("/other/entity"))
                .andExpect(jsonPath("$.result.id").isNumber());
    }
}
