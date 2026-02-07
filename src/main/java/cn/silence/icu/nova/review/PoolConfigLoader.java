package cn.silence.icu.nova.review;

import cn.silence.icu.nova.review.model.Person;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2026/02/05 20:32 周四
 */
public class PoolConfigLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // 单池配置
    public static List<Person> loadSinglePool(String filePath) throws IOException {
        JsonNode root = MAPPER.readTree(new File(filePath));
        return MAPPER.convertValue(root.get("people"), MAPPER.getTypeFactory()
                .constructCollectionType(List.class, Person.class));
    }

    // 双池配置
    public static Map<String, List<Person>> loadDualPool(String filePath) throws IOException {
        JsonNode root = MAPPER.readTree(new File(filePath));
        Map<String, List<Person>> pools = new HashMap<>();

        pools.put("poolA", MAPPER.convertValue(root.get("poolA").get("people"),
                MAPPER.getTypeFactory().constructCollectionType(List.class, Person.class)));
        pools.put("poolB", MAPPER.convertValue(root.get("poolB").get("people"),
                MAPPER.getTypeFactory().constructCollectionType(List.class, Person.class)));
        return pools;
    }
}
