package cn.swao.jinyao.model;

import java.util.*;

import org.springframework.data.annotation.Id;

public class SquareDance extends BaseCatch {

    @Id
    public String id;

    public List<Object> jsonString;
    public String squareDanceId;
    public String alias;
    public String title;
    public String team;
    public String name;
    public String type;
    public String thumb;
    public String play_count;
    public String publish_time;
    public String song_title;
    public String jsonUrl;
    public String video_url;
    public String song_html;
    public String down_video_url;
    public int status;
    public List<Map<String, String>> songList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Object> getJsonString() {
        return jsonString;
    }

    public void setJsonString(List<Object> jsonString) {
        this.jsonString = jsonString;
    }

    public String getSquareDanceId() {
        return squareDanceId;
    }

    public void setSquareDanceId(String squareDanceId) {
        this.squareDanceId = squareDanceId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getPlay_count() {
        return play_count;
    }

    public void setPlay_count(String play_count) {
        this.play_count = play_count;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public String getSong_title() {
        return song_title;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public String getJsonUrl() {
        return jsonUrl;
    }

    public void setJsonUrl(String jsonUrl) {
        this.jsonUrl = jsonUrl;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getSong_html() {
        return song_html;
    }

    public void setSong_html(String song_html) {
        this.song_html = song_html;
    }

    public String getDown_video_url() {
        return down_video_url;
    }

    public void setDown_video_url(String down_video_url) {
        this.down_video_url = down_video_url;
    }

    public List<Map<String, String>> getSongList() {
        return songList;
    }

    public void setSongList(List<Map<String, String>> songList) {
        this.songList = songList;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "SquareDance [id=" + id + ", jsonString=" + jsonString + ", squareDanceId=" + squareDanceId + ", alias=" + alias + ", team=" + team + ", name=" + name + ", type=" + type + ", thumb=" + thumb + ", play_count=" + play_count + ", publish_time=" + publish_time + ", song_title=" + song_title + ", jsonUrl=" + jsonUrl + ", video_url=" + video_url + ", song_html=" + song_html + ", down_video_url=" + down_video_url + ", status=" + status + ", songList=" + songList + "]";
    }

}
