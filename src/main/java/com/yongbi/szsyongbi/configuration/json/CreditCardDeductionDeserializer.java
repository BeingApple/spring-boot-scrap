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
import java.util.HashMap;
import java.util.Map;

public class CreditCardDeductionDeserializer extends JsonDeserializer<Map<YearMonth, BigDecimal>> {
    @Override
    public Map<YearMonth, BigDecimal> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        final var map = new HashMap<YearMonth, BigDecimal>();

        final var codec = p.getCodec();
        final var node = codec.readTree(p);

        int year;
        final var yearNode = node.get("year");
        if (yearNode.asToken().isNumeric()) {
            year = ((JsonNode) yearNode).asInt();
        } else {
            throw new JsonParseException(p, "Expect Long Value");
        }

        final var arrayNode = node.get("month");
        if (arrayNode.isArray()) {
            for (final JsonNode n : (ArrayNode) arrayNode) {
                final var fields = n.fields();
                while(fields.hasNext()) {
                    final var monthNode = fields.next();

                    final var month = Integer.parseInt(monthNode.getKey());
                    final var deduction = new BigDecimal(monthNode.getValue().asText().replaceAll(",", ""));

                    map.put(YearMonth.of(year, month), deduction);
                }
            }
        } else {
            throw new JsonParseException(p, "Expect Array Value");
        }

        return map;
    }
}
