package com.min.smalltalk.wedget.Wheel;

import java.text.SimpleDateFormat;

/**
 * Created by Min on 2016/12/7.
 */

public class JudgeDate {
    public static  boolean isDate(String str_input,String rDateFormat){
        if (!isNull(str_input)) {
            SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
            formatter.setLenient(false);
            try {
                formatter.format(formatter.parse(str_input));
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }
    public static boolean isNull(String str){
        if(str==null)
            return true;
        else
            return false;
    }
}
