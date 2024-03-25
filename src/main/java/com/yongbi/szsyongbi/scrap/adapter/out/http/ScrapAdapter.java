package com.yongbi.szsyongbi.scrap.adapter.out.http;

import com.yongbi.szsyongbi.scrap.adapter.out.http.payload.ScrapData;
import com.yongbi.szsyongbi.scrap.adapter.out.http.payload.ScrapRequestPayload;
import com.yongbi.szsyongbi.scrap.application.port.out.RequestScrapPort;
import org.springframework.stereotype.Service;

@Service
public class ScrapAdapter implements RequestScrapPort {
    private final ScrapFeignClient scrapFeignClient;

    public ScrapAdapter(ScrapFeignClient scrapFeignClient) {
        this.scrapFeignClient = scrapFeignClient;
    }

    @Override
    public ScrapData get(String name, String regNo) {
        final var payload = new ScrapRequestPayload(name, regNo);
        final var result = scrapFeignClient.getScrap(payload);

        if (!result.getStatus().equals("success")) {
            throw new RuntimeException(result.getErrors().getMessage());
        }

        return result.getData();
    }
}
