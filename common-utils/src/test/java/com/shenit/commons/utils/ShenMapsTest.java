package com.shenit.commons.utils;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jiangnan on 23/07/2017.
 */
public class ShenMapsTest {
    /**
     * 测试超级get
     */
    @Test
    public void testGet(){
        Payload maps = Payload.wrap(
                "a1",Payload.wrap("a2",new Payload[]{
                        Payload.wrap("a3",1,"b3",2,"c3",3),
                        Payload.wrap("a3",2,"b3",3,"c3",4)}),
                "b1", Lists.newArrayList(1,2,3),
                "c1", "simple case");
        Assert.assertEquals(Integer.valueOf(1), ShenMaps.get(maps,null,"a1","a2",0,"a3"));
        Assert.assertEquals(Integer.valueOf(2), ShenMaps.get(maps,null,"b1",1));
        Assert.assertNull(ShenMaps.get(maps,null,"b1",5));    //超出索引大小的处理
        Assert.assertEquals("simple case", ShenMaps.get(maps,null,"c1"));

    }
}
