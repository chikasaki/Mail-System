package com.yao.thread;

import com.yao.dao.AttachmentDao;
import com.yao.entity.Attachment;
import com.yao.util.DetectUtil;
import com.yao.util.TikaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

//线程实现不足：当用户改变关键词后，旧线程不会中断
@Slf4j
public class AttachmentThread extends Thread{

    private HashSet<Integer> found;
    private String key;
    public static HashSet<Integer> emailIds;

    static{
        emailIds = new HashSet<>();
    }

    public AttachmentThread(String key, HashSet<Integer> found){
        this.key = key;
        this.found = found;
    }

    @Override
    public void run() {
        emailIds.clear();

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        AttachmentDao attachmentDao = (AttachmentDao) applicationContext.getBean("attachmentDao");

        List<Attachment> attachmentList = attachmentDao.selectAll();
        Tika tika = TikaUtil.getTika();
        for(Attachment attachment: attachmentList){
            //如果当前线程在外部被中断，则直接跳出循环
            if(isInterrupted()) break;

            if(found.contains(attachment.getEmailId()) ||
                    emailIds.contains(attachment.getEmailId())){
                continue;
            }
            String fileName = attachment.getFileName();
            File file = new File(fileName);
            try {
                if (!file.exists()) {
                    throw new RuntimeException();
                }
                String content = tika.parseToString(file).trim();
                if(DetectUtil.detect(key, content)){
                    emailIds.add(attachment.getEmailId());
                }

            } catch(RuntimeException e){
                log.warn("附件" + file.getName() + "在磁盘中已删除");
            } catch (TikaException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
