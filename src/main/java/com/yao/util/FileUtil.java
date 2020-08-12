package com.yao.util;

import com.yao.entity.Attachment;

import java.io.File;
import java.util.List;

public class FileUtil {

    public static void findAllAttachments(List<Attachment> list, File file, int emailId){
        if(file == null || !file.exists()) return;
        if(file.isDirectory()){
            File[] subFiles = file.listFiles();
            for(File subFile: subFiles){
                findAllAttachments(list, subFile, emailId);
            }
        }else{
            Attachment attachment = new Attachment();
            attachment.setFileName(file.getAbsolutePath());
            attachment.setEmailId(emailId);
            list.add(attachment);
        }
    }
}
