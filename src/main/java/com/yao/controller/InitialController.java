package com.yao.controller;

import com.yao.dto.EmailState;
import com.yao.dto.JsonData;
import com.yao.entity.EmailWithBlobs;
import com.yao.enums.EmailStateEnum;
import com.yao.service.EmailService;
import com.yao.util.LoadMailUtil;
import com.yao.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Controller
@RequestMapping("/")
@Slf4j
public class InitialController {

    @Autowired
    private EmailService emailService;

    @RequestMapping("/initialize")
    @ResponseBody
    public JsonData initialize(){
        JsonData jsonData = null;
        if(saveAllEmails()){
            jsonData = JsonData.success();
        }else{
            jsonData = JsonData.fail();
        }
        return jsonData;
    }

    private File[] getAllEmails(){
//        System.out.println(PathUtil.getSourceRepo());
        File dir = new File(PathUtil.getSourceRepo());
        return dir.listFiles();
    }

    private MimeMessage getMsg(File file) throws FileNotFoundException, MessagingException {
        InputStream fis = new FileInputStream(file);
        Session session = Session.getDefaultInstance(System.getProperties(), null);
        MimeMessage msg = new MimeMessage(session, fis);

        return msg;
    }

    private boolean saveAllEmails(){
        File[] emails = getAllEmails();
//        System.out.println(emails);

        for(File email: emails){
            try {
//                System.out.println(this.getClass()+":遍历到邮件--" +email.getName());
                MimeMessage msg = getMsg(email);

                EmailWithBlobs fullEmail = LoadMailUtil.loadMessage(msg);
//                System.out.println(this.getClass()+":邮件内容分析完毕，标题：" +fullEmail.getTitle());
                EmailState state = emailService.add(fullEmail);
                if(state.getState() != EmailStateEnum.SUCCESS.getState()){
                    throw new RuntimeException(state.getStateInfo());
                }
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
                return false;
            } catch (MessagingException e) {
                log.error(e.getMessage());
                return false;
            } catch (RuntimeException e){
                log.error(e.getMessage());
                return false;
            }
        }
        return true;
    }
}
