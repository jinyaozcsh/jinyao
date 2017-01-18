package cn.swao.jinyao.util;

import java.io.FileWriter;

public class FileUtils {

    public static synchronized void putFile(String filePath, String str) {
        if (str == null) {
            return;
        }
        try {
            FileWriter out = new FileWriter(filePath, true);
            out.write(str + "\n");
            out.flush();
            out.close();
        } catch (Exception e) {
        }

    }

}
