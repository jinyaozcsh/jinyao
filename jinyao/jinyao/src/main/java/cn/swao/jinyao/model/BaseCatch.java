package cn.swao.jinyao.model;

import java.util.Date;

public class BaseCatch {

    protected String sourceUrl;

    protected Date createTime;

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
