package com.yao.service.impl;

import com.yao.dao.AttachmentDao;
import com.yao.dao.EmailDao;
import com.yao.dto.EmailState;
import com.yao.dto.JsonData;
import com.yao.dto.QueryDto;
import com.yao.entity.Attachment;
import com.yao.entity.EmailWithBlobs;
import com.yao.enums.EmailStateEnum;
import com.yao.service.EmailService;
import com.yao.thread.AttachmentThread;
import com.yao.util.FileUtil;
import com.yao.util.PathUtil;
import com.yao.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service("emailService")
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailDao emailDao;
    @Autowired
    private AttachmentDao attachmentDao;

    private AttachmentThread thread;

    @Transactional
    @Override
    public EmailState add(EmailWithBlobs email) {
        try {
//            System.out.println("开始往数据库中添加邮件：" + email.getTitle());
            int num = emailDao.insert(email);
            if(num <= 0) throw new RuntimeException("Email 插入数据库错误");
//            System.out.println("开始检查附件：" + email.getTitle());

            List<Attachment> attachments = email.getAttachments();
            List<Attachment> newList = new ArrayList<>();
//            System.out.println("附件大小：" + attachments.size());
            for(Attachment attachment: attachments){
                String fileName = attachment.getFileName();
                if(fileName.endsWith(".zip")){
                    int index = fileName.lastIndexOf(".");
                    String addr = PathUtil.getRepo() + email.getId() + File.separator;
                    File zipDir = new File(addr);
                    if(!zipDir.exists()) zipDir.mkdirs();
                    File srcFile = new File(fileName);
                    ZipUtil.unZip(srcFile, addr);

                    File dir = new File(addr);
                    FileUtil.findAllAttachments(newList, dir, email.getId());
                }else{
                    attachment.setEmailId(email.getId());
                    newList.add(attachment);
                }
            }
//            System.out.println("附件检查完毕");

            if(newList.size() > 0) {
                num = attachmentDao.batchInsert(newList);
                if (num != newList.size()) throw new RuntimeException("批量插入附件出错");
            }
//            System.out.println("邮件添加完毕");

            EmailState state = new EmailState(EmailStateEnum.SUCCESS);
            return state;
        } catch(Exception e){
            System.out.println("发生错误:"+e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public EmailState getByKey(QueryDto queryDto) {
        try {
            List<EmailWithBlobs> emailList = emailDao.selectByKey(queryDto);

            EmailState retState = new EmailState(EmailStateEnum.SUCCESS, emailList);
            retState.setCount(emailList.size());

            //另开线程进行附件查找
            if(queryDto.getIsAttachment() == 1){
                HashSet<Integer> found = new HashSet<>();
                for(EmailWithBlobs email: emailList){
                    found.add(email.getId());
                }
                if(thread != null && thread.isAlive()){
                    thread.interrupt();
                }
                thread = new AttachmentThread(queryDto.getKey(), found);
                thread.start();
            }
            return retState;
        } catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    //获取附件查询的邮件列表
    @Override
    public EmailState getByAttachment() {
        while(thread.isAlive());
        System.out.println("start");
        HashSet<Integer> emailIds = AttachmentThread.emailIds;

        List<EmailWithBlobs> emailList = new ArrayList<>();
        for(int id: emailIds){
            try {
                EmailWithBlobs email = emailDao.selectById(id);
                emailList.add(email);
            } catch (Exception e){
                log.error(e.getMessage());
                throw new RuntimeException("数据库查询出错");
            }
        }
        EmailState state = new EmailState(EmailStateEnum.SUCCESS, emailList);
        return state;
    }

    @Override
    public EmailState getById(int emailId) {
        try {
            EmailWithBlobs email = emailDao.selectById(emailId);
            EmailState state = new EmailState(EmailStateEnum.SUCCESS, email);
            return state;
        } catch (Exception e){
            log.error(e.getMessage());
            return new EmailState(EmailStateEnum.FAIL);
        }
    }
}
