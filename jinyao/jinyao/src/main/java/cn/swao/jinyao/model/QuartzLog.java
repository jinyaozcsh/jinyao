package cn.swao.jinyao.model;

import java.util.Date;

import org.springframework.data.annotation.Id;


public class QuartzLog {
    @Id
    private String id;
    /**
     * 调度时间
     */
    private Date dateTime;

    private String quartzName;
    /**
     * 执行结果 1：成功，2：失败
     */
    private Integer result;

    /**
     * 操作类型 0：定时调度，1：手工调度，2：定时任务操作
     */
    private Integer opType;
    /**
     * 操作日志
     */
    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getQuartzName() {
        return quartzName;
    }

    public void setQuartzName(String quartzName) {
        this.quartzName = quartzName;
    }

    @Override
    public String toString() {
        return "QuartzLog [id=" + id + ", dateTime=" + dateTime + ", quartzName=" + quartzName + ", result=" + result + ", opType=" + opType + ", message=" + message + "]";
    }

    public QuartzLog(Date dateTime, String quartzName, Integer result, Integer opType, String message) {
        super();
        this.dateTime = dateTime;
        this.quartzName = quartzName;
        this.result = result;
        this.opType = opType;
        this.message = message;
    }

    public QuartzLog() {
        super();
    }
}
