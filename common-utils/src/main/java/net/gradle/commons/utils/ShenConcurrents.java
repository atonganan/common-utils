package net.gradle.commons.utils;

import java.util.concurrent.TimeUnit;

/**
 * 并发任务
 * Created by jiangnan on 21/07/2017.
 */
public final class ShenConcurrents {
    /**
     * 睡一段时间
     * @param duration
     * @param unit
     */
    public static void sleepQuietly(int duration, TimeUnit unit){
        try{
            Thread.sleep(unit.toMillis(duration));
        }catch(InterruptedException t){
        }
    }

}
