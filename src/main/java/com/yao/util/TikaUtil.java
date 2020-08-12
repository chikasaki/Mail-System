package com.yao.util;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

public class TikaUtil {

    private static Tika tika;

    private TikaUtil(){
    }

    public static Tika getTika(){
        if(tika == null) tika = new Tika();
        return tika;
    }
}
