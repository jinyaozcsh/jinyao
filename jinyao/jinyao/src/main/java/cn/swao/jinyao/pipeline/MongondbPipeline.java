package cn.swao.jinyao.pipeline;

import java.util.Hashtable;

/**
 * 保存到mongodb通道
 * 
 * @author ShenJX
 * @date 2017年2月6日
 * @desc
 */
public interface MongondbPipeline {

    void save(String className, Hashtable<Object, Object> table);
}
