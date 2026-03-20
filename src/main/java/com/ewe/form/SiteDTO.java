package com.ewe.form;

public class SiteDTO {
    private Long siteId;
    private String siteName;

    public SiteDTO(Long siteId, String siteName) {
        this.siteId = siteId;
        this.siteName = siteName;
    }

    public Long getSiteId() {
        return siteId;
    }

    public String getSiteName() {
        return siteName;
    }
}

