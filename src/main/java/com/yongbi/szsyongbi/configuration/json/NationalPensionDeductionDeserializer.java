package com.yongbi.szsyongbi.configuration.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class NationalPensionDeductionDeserializer extends JsonDeserializer<Map<YearMonth, BigDecimal>> {
    @Override
    public Map<YearMonth, BigDecimal> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        final var map = new HashMap<YearMonth, BigDecimal>();

        final var codec = p.getCodec();
        final var node = codec.readTree(p);

        if (node.isArray()) {
            for (final JsonNode n : (ArrayNode) node) {
                final var key = YearMonth.parse(n.get("월").asText(), DateTimeFormatter.ofPattern("yyyy-MM"));
                final var deduction = new BigDecimal(n.get("공제액").asText().replaceAll(",", ""));

                map.put(key, deduction);
            }
        } else {
            throw new JsonParseException(p, "Expect Array Value");
        }

        return map;
    }
}
