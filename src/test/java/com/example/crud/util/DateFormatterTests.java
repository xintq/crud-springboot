package com.example.crud.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DateFormatterTests {
    @Test
    public void testNow() throws Exception {
        String r = DateFormatter.nowAsyyyyMMddHHmmss();
        System.out.println(r);
        Assert.assertNotNull(r);
        Assert.assertSame(14, r.length());
    }
}
