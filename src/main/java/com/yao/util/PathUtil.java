package com.yao.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class PathUtil {

    private static String repo;
    private static String sourceRepo;

    private PathUtil(String repo, String sourceRepo){

        PathUtil.repo = repo;
        PathUtil.sourceRepo = sourceRepo;
    }

    public static String getRepo(){
        return repo;
    }

    public static String getSourceRepo(){
        return sourceRepo;
    }
}
