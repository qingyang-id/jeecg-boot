package org.jeecg.modules.business.config;

import org.jeecg.common.api.vo.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Map;

/**
 * ERP 接口返回的 id / *Id 整数字段转成字符串。
 * <p>
 * 明细表主键是 19 位 Long（约 1e18），超过 JS Number 安全整数，前端会丢精度。3.0 用全局
 * Long→String 解决；3.9.5 的全局 ObjectMapper 不再这样做，这里只对 org.jeecg.modules.business
 * 包下的 controller 生效，不影响上游接口。DictAspect 会把分页记录换成 JSONObject（反射取值），
 * 字段注解对它无效，所以在写响应前对整棵 JSON 树按字段名处理。
 * 反序列化不受影响：Jackson 默认把 "123" 转成 Long。
 */
@RestControllerAdvice(basePackages = "org.jeecg.modules.business")
public class ErpLongIdToStringAdvice implements ResponseBodyAdvice<Object> {

    private final JsonMapper jsonMapper;

    public ErpLongIdToStringAdvice(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Result && MediaType.APPLICATION_JSON.isCompatibleWith(selectedContentType)) {
            Result<Object> result = (Result<Object>) body;
            if (result.getResult() != null) {
                JsonNode tree = jsonMapper.valueToTree(result.getResult());
                stringifyIds(tree);
                result.setResult(tree);
            }
        }
        return body;
    }

    static boolean isIdField(String name) {
        return "id".equals(name) || name.endsWith("Id");
    }

    private static void stringifyIds(JsonNode node) {
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            for (Map.Entry<String, JsonNode> e : new ArrayList<>(obj.properties())) {
                JsonNode v = e.getValue();
                if (v.isIntegralNumber() && isIdField(e.getKey())) {
                    obj.put(e.getKey(), v.asString());
                } else {
                    stringifyIds(v);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                stringifyIds(child);
            }
        }
    }
}
