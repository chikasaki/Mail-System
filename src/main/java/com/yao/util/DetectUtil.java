package com.yao.util;

//使用kmp算法对附件文本内容进行关键词检测
public class DetectUtil {

    public static boolean detect(String key, String content){
        if(key == null || content == null) return true;

        int n = key.length();
        int[] next = new int[n];
        for (int i = 1; i < n; i++) {
            int preL = next[i - 1];
            if(preL >= 1 && key.charAt(preL) != key.charAt(i)){
                preL = next[preL - 1];
            }
            if(key.charAt(preL) == key.charAt(i)) next[i] = preL + 1;
        }

        int cP = 0, kP = 0;
        while(cP < content.length()){
            if(content.charAt(cP) == key.charAt(kP)){
                cP ++;
                kP ++;
            }else if(kP == 0){
                cP ++;
            }else{
                kP = next[kP - 1];
            }

            if(kP == n) {
                return true;
            }
        }
        return false;
    }
}
