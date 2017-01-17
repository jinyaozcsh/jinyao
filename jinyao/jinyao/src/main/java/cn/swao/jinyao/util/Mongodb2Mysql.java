package cn.swao.jinyao.util;

import java.util.*;

import org.bson.Document;

import com.mongodb.client.*;

import cn.swao.baselib.util.JSONUtils;

public class Mongodb2Mysql {

    /**
     * 
     * @param documents mongodb的collection对象
     * @param filds key:mongodb的字段，value对应实体的字段
     * @return
     */
    public static List<Map<String, Object>> repalyList(MongoCollection<Document> documents, Map<String, String> filds) {

        MongoCursor<Document> it = documents.find().iterator();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        while (it.hasNext()) {
            Document next = it.next();
            Map<String, Object> map = new HashMap<String, Object>();
            for (String key : filds.keySet()) {
                Object object = next.get(key);
                map.put(filds.get(key), object);
            }
            list.add(map);
        }
        return list;

    }

    /**
     * 
     * @param documents mongodb的collection对象
     * @param filds key:mongodb的字段，value对应实体的字段
     * @param entity 实体名称
     * @return
     */
    public static <E> List<E> repalyList(MongoCollection<Document> documents, Map<String, String> filds, Class<E> entity) {

        List<Map<String, Object>> repalyList = repalyList(documents, filds);
        List<E> list = new ArrayList<E>();
        for (Map<String, Object> map : repalyList) {
            E e = JSONUtils.fromJson(JSONUtils.toJson(map), entity);
            list.add(e);
        }
        return list;
    }

}
