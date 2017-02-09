package cn.swao.jinyao.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

/**
 * @author ShenJX
 * @date 2017年2月6日
 * @desc 活动模型
 */
public class Activity extends BaseCatch {

    public static final String TYPE_COMMUNITY = "SQHD";

    @Id
    private String id;
    private String title;
    private String address;
    private Date beginTime;
    private Date endTime;
    private String coverImage;
    private String phone;
    private String type;
    private String region;
    // 原始内容
    private String originalContent;
    // 清洗过的内容
    private String cleanedContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getCleanedContent() {
        return cleanedContent;
    }

    public void setCleanedContent(String cleanedContent) {
        this.cleanedContent = cleanedContent;
    }

    @Override
    public String toString() {
        return "Activity [id=" + id + ", title=" + title + ", address=" + address + ", beginTime=" + beginTime + ", endTime=" + endTime + ", coverImage=" + coverImage + ", phone=" + phone + ", sourceUrl=" + sourceUrl + ", type=" + type + ", region=" + region + ", originalContent=" + originalContent + ", cleanedContent=" + cleanedContent + "]";
    }
}
