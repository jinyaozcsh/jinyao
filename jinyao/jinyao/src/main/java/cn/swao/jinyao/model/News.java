package cn.swao.jinyao.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

/**
 * 
 * @author ShenJX
 * @date 2017年2月6日
 * @desc 新闻模型
 */
public class News extends BaseCatch {
    public static final String TYPE_NEWS_ACTUAL = "actual";
    public static final String TYPE_NEWS_SOUND = "sound";

    public static final String TYPE_MEDIA_VIDEO = "video";
    public static final String TYPE_MEDIA_AUDIO = "audio";

    @Id
    private String id;
    private String title;
    private List<String> coverImage;
    // 原始内容
    private String originalContent;
    // 清洗过的内容
    private String cleanedContent;
    // 区域
    private String region;
    // 媒体类型
    private String mediaType;
    // 新闻类型
    private String newsType;
    // 媒体资源链接
    private String mediaSourceUrl;
    // 摘要
    private String summary;
    // 发布者
    private String publisher;
    // 新闻时间
    private String newsTime;

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

    public List<String> getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(List<String> coverImage) {
        this.coverImage = coverImage;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getNewsType() {
        return newsType;
    }

    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public String getMediaSourceUrl() {
        return mediaSourceUrl;
    }

    public void setMediaSourceUrl(String mediaSourceUrl) {
        this.mediaSourceUrl = mediaSourceUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public News(String title, List<String> coverImage, String originalContent, String cleanedContent, String sourceUrl, String mediaType, String newsType, String publisher, String newsTime) {
        super();
        this.title = title;
        this.coverImage = coverImage;
        this.originalContent = originalContent;
        this.cleanedContent = cleanedContent;
        this.sourceUrl = sourceUrl;
        this.mediaType = mediaType;
        this.newsType = newsType;
        this.publisher = publisher;
        this.newsTime = newsTime;
    }

    public News() {
        super();
    }

    @Override
    public String toString() {
        return "News [id=" + id + ", title=" + title + ", coverImage=" + coverImage + ", originalContent=" + originalContent + ", cleanedContent=" + cleanedContent + ", region=" + region + ", sourceUrl=" + sourceUrl + ", mediaType=" + mediaType + ", newsType=" + newsType + ", mediaSourceUrl=" + mediaSourceUrl + ", summary=" + summary + ", publisher=" + publisher + ", createTime=" + createTime + ", newsTime=" + newsTime + "]";
    }
}
