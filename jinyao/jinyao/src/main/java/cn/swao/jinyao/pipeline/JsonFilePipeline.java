package cn.swao.jinyao.pipeline;

import java.io.*;

import org.slf4j.*;

import cn.swao.baselib.util.JSONUtils;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.*;

/**
 * @author : kangwg 2017年2月14日
 *
 */
public class JsonFilePipeline implements Pipeline {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private File file;

    public JsonFilePipeline(String filePath) {
        this(new File(filePath));
    }

    public JsonFilePipeline(File file) {
        this.file = file;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        Object object = resultItems.getAll();
        String json = JSONUtils.toJson(object);
        synchronized (file) {
            FileWriter out = null;
            try {
                out = new FileWriter(file, true);
                out.write(json + "\n");
                out.flush();
            } catch (Exception e) {
                logger.warn("write file error", e);
            } finally {
                if (out != null)
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

}
