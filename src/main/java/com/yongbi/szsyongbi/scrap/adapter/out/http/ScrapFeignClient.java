package com.yongbi.szsyongbi.scrap.adapter.out.http;

import com.yongbi.szsyongbi.configuration.FeignConfig;
import com.yongbi.szsyongbi.scrap.adapter.out.http.payload.ScrapRequestPayload;
import com.yongbi.szsyongbi.scrap.adapter.out.http.payload.ScrapResponsePayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "scrapFeignClient",
        qualifiers = "scrapFeignClient",
        url = "https://codetest-v4.3o3.co.kr",
        configuration = FeignConfig.class
)
public interface ScrapFeignClient {
    @PostMapping(value="/scrap",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ScrapResponsePayload getScrap(@RequestBody ScrapRequestPayload payload);
}
