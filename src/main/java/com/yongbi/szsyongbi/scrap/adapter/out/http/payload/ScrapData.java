package com.yongbi.szsyongbi.scrap.adapter.out.http.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.yongbi.szsyongbi.configuration.json.CreditCardDeductionDeserializer;
import com.yongbi.szsyongbi.configuration.json.NationalPensionDeductionDeserializer;
import com.yongbi.szsyongbi.configuration.json.NumberFormatStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ScrapData {
    @JsonProperty(value = "이름")
    private String name;

    @JsonProperty(value = "종합소득금액")
    private BigDecimal totalIncome;

    @JsonProperty(value = "소득공제")
    private IncomeDeduction incomeDeduction;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class IncomeDeduction {
        @JsonProperty(value = "세액공제")
        @JsonDeserialize(using = NumberFormatStringDeserializer.class)
        private BigDecimal taxDeduction;

        @JsonProperty(value = "국민연금")
        @JsonDeserialize(using = NationalPensionDeductionDeserializer.class)
        private Map<YearMonth, BigDecimal> nationalPensionDeduction;

        @JsonProperty(value = "신용카드소득공제")
        @JsonDeserialize(using = CreditCardDeductionDeserializer.class)
        private Map<YearMonth, BigDecimal> creditCardDeduction;

        @Override
        public String toString() {
            return "IncomeDeduction{" +
                    "taxDeduction=" + taxDeduction +
                    ", nationalPensionDeduction=" + nationalPensionDeduction +
                    ", creditCardDeduction=" + creditCardDeduction +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ScrapResponsePayload{" +
                "name='" + name + '\'' +
                ", totalIncome=" + totalIncome +
                ", incomeDeduction=" + incomeDeduction +
                '}';
    }
}
