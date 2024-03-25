package com.yongbi.szsyongbi.scrap.application.port.in;

public record ScrapSaveCommand(Long id, boolean overwrite) {
    public ScrapSaveCommand(Long id) {
        this(id, false);
    }
}
