package com.yao.util;

import com.yao.entity.Attachment;
import com.yao.entity.Email;
import com.yao.entity.EmailWithBlobs;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LoadMailUtil {

    public static EmailWithBlobs loadMessage(MimeMessage msg){
        EmailWithBlobs email = new EmailWithBlobs();

        try {
            email.setTitle(msg.getSubject());
            email.setFrom(((InternetAddress)msg.getFrom()[0]).getAddress());
            email.setSendTime(msg.getSentDate());

            List<Attachment> attachments = new ArrayList<>();
            System.out.println("准备分析邮件文件内容，标题："+email.getTitle());
            String content = getContent(msg, attachments);
            email.setContent(HtmlUtil.html2Str(content));
            System.out.println("邮件文件内容分析完毕，标题："+email.getTitle());
            email.setAttachments(attachments);

        } catch (MessagingException e) {
            log.warn("Message->Email过程出错");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return email;
    }

    private static String getContent(Part part, List<Attachment> attachments) throws MessagingException, IOException {
        StringBuilder sb = new StringBuilder();
        if(part.isMimeType("text/plain") || part.isMimeType("text/html")){
            sb.append(part.getContent());
        }else if(part.isMimeType("multipart/*")){
            Multipart multipart = (Multipart) part.getContent();
            int count = multipart.getCount();
            if(part.isMimeType("multipart/alternative")){
                //文本 + 超文本
                if(count > 1){
                    sb.append(getContent(multipart.getBodyPart(1), attachments));
                }else{
                    sb.append(getContent(multipart.getBodyPart(0), attachments));
                }
            }else if(part.isMimeType("multipart/related")){
                // 无视所有内嵌文件，只找文本
                for(int i = 0; i < count; i ++){
                    Part subPart = multipart.getBodyPart(i);
                    if(subPart.isMimeType("multipart/alternative")){
                        sb.append(getContent(subPart, attachments));
                    }
                }
            }else {
                // 带附件的邮件
                for(int i = 0; i < count; i ++){
                    Part subPart = multipart.getBodyPart(i);
                    String disposition = subPart.getDisposition();
                    if(disposition != null && (disposition.equals(Part.ATTACHMENT) ||
                            disposition.equals(Part.INLINE))){
                        String fileName = MimeUtility.decodeText(subPart.getFileName());
                        saveFile(fileName, subPart.getInputStream(), attachments);
                    }
                    if(subPart.isMimeType("multipart/alternative")){
                        sb.append(getContent(subPart, attachments));
                    }
                }
            }
        }else if(part.isMimeType("message/rfc822")){
            sb.append(getContent((Part) part.getContent(), attachments));
        }

        return sb.toString();
    }

    private static void saveFile(String fileName, InputStream is, List<Attachment> attachments) throws IOException {
        String absoluteFilename = PathUtil.getRepo() + fileName;
        File file = new File(absoluteFilename);

        //防重名
        int index = 0;
        int pIndex = absoluteFilename.lastIndexOf(".");
        String suffix = absoluteFilename.substring(pIndex);
        while(file.exists()){
            file = new File(absoluteFilename.substring(0, pIndex) + (index++) + suffix);
        }

        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(file)
        );

        int len = -1;
        while((len = bis.read()) != -1){
            bos.write(len);
            bos.flush();
        }
        bis.close();
        bos.close();

        Attachment attachment = new Attachment();
        attachment.setFileName(file.getAbsolutePath());
        attachments.add(attachment);
    }
}
