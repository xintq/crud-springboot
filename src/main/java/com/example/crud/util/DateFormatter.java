/*
 * Copyright (c) K.X(Kevin Xin) 2017.
 * Find more details in http://xintq.net
 *
 */

package com.example.crud.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
    public static String nowAsyyyyMMddHHmmss(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date());
    }
}
