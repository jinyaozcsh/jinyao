package cn.swao.jinyao.model;

import org.springframework.data.annotation.Id;

public class Quartz {

    @Id
    private String id;

    private String method;
    private String startTime;
    private String period;
    private String explain;
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Quartz(String method, String startTime, String period, String explain) {
        super();
        this.method = method;
        this.startTime = startTime;
        this.period = period;
        this.explain = explain;
    }

    public Quartz() {
        super();
    }

    @Override
    public String toString() {
        return "Quartz [id=" + id + ", method=" + method + ", startTime=" + startTime + ", period=" + period + ", explain=" + explain + ", status=" + status + "]";
    }

}
