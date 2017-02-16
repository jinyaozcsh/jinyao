package cn.swao.jinyao.antiseptic;

import java.util.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.swao.baselib.util.NetUtils;
import cn.swao.framework.def.StatusEnum;
import cn.swao.jinyao.crawl.NetPoxyService;
import cn.swao.jinyao.model.SquareDance;
import cn.swao.jinyao.repository.SquareDanceRepository;

/**
 * @author : kangwg 2017年2月16日
 *
 */
@Service
public class SquareDanceAntiseptic {
    private static Logger log = LoggerFactory.getLogger(SquareDanceAntiseptic.class);
    public int threadNum = 5;

    public int checkNum = 10;

    @Autowired
    private NetPoxyService netPoxyService;

    @Autowired
    private SquareDanceRepository squareDanceRepository;

    public void run(int threadNum, int checkNum) {
        this.threadNum = threadNum;
        this.checkNum = checkNum;
        run();
    }

    public void run() {
        List<SquareDance> squareDanceList = squareDanceRepository.findByStatusNot(StatusEnum.DELETE.getValue());
        int size = squareDanceList.size();
        if (size > threadNum) {
            size = size / threadNum;
            for (int i = 0; i < threadNum; i++) {
                List<SquareDance> subList = squareDanceList.subList(i * size, (i != threadNum - 1) ? (i + 1) * size : squareDanceList.size());
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        squareDanceAntiseptic(subList);
                    }
                }).start();
            }
        } else {
            squareDanceAntiseptic(squareDanceList);
        }
    }

    public void squareDanceAntiseptic(List<SquareDance> squareDanceList) {
        for (SquareDance squareDance : squareDanceList) {
            try {
                checkSquarlDance(squareDance);
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("check one data squareDance faile {}", squareDance);
                e.printStackTrace();
            }
        }
    }

    public void checkSquarlDance(SquareDance squareDance) {
        boolean flag = false;
        int status = squareDance.getStatus();
        String video_url = squareDance.getVideo_url();
        Integer videoCheckCount = squareDance.getVideoCheckCount();
        if (videoCheckCount == null) {
            videoCheckCount = 0;
        }
        List<Map<String, Object>> songList = squareDance.getSongList();

        // 校验视频
        boolean videoUrl = /* netPoxyService */NetUtils.isHttpExist(video_url);
        if (videoUrl) {
            if (status == StatusEnum.INVALID.getValue()) {
                squareDance.setStatus(StatusEnum.VALID.getValue());
                squareDance.setVideoCheckCount(0);
                flag = true;
            }
        } else {
            flag = true;
            if (status == StatusEnum.VALID.getValue()) {
                squareDance.setStatus(StatusEnum.INVALID.getValue());
                squareDance.setVideoCheckCount(1);
            } else if (videoCheckCount < checkNum) {
                squareDance.setVideoCheckCount(++videoCheckCount);
            } else if (videoCheckCount >= checkNum) {
                squareDance.setStatus(StatusEnum.DELETE.getValue());
            }
        }

        // 校验音频
        List<Map<String, Object>> newSong = new ArrayList<Map<String, Object>>();
        boolean songflag = false;
        for (int i = 0; i < songList.size(); i++) {
            Map<String, Object> song = songList.get(i);
            Object content = song.get("content");
            if (content == null) {
                song.put("content", 0);
                content = 0;
            }
            Integer con = (Integer) content;
            String down_song_url = (String) song.get("down_song_url");
            boolean songUrl = /* netPoxyService */NetUtils.isHttpExist(down_song_url);
            if (songUrl) {
                if (con != 0) {
                    song.put("content", 0);
                    flag = true;
                }
                newSong.add(song);
                songflag = true;
            } else {
                flag = true;
                if (con < checkNum) {
                    song.put("content", ++con);
                    newSong.add(song);
                }
            }

        }
        if (flag) {
            if (newSong.size() <= 0) {
                squareDance.setStatus(StatusEnum.DELETE.getValue());
            } else if (!songflag && squareDance.getStatus() != StatusEnum.DELETE.getValue()) {
                squareDance.setStatus(StatusEnum.INVALID.getValue());
            }
            squareDance.setSongList(newSong);

            if (status != squareDance.getStatus()) {
                squareDance.setCreateTime(new Date());
            }
            this.squareDanceRepository.save(squareDance);
        }
    }

}
