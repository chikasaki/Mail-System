import com.yao.dao.EmailDao;
import com.yao.dto.EmailState;
import com.yao.dto.QueryDto;
import com.yao.entity.EmailWithBlobs;
import com.yao.service.EmailService;
import com.yao.util.DetectUtil;
import com.yao.util.HtmlUtil;
import com.yao.util.TikaUtil;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:applicationContext.xml",
        "classpath:springmvc-servlet.xml"
})
public class TestAPI {

    @Autowired
    private EmailDao emailDao;
    @Resource(name="emailService")
    private EmailService emailService;

    @Test
    public void testAPI(){
        String fileName = "D:\\mail\\test_attach.eml";
        try {
            InputStream fis = new FileInputStream(fileName);
            Session session = Session.getDefaultInstance(System.getProperties(), null);
            MimeMessage msg = new MimeMessage(session, fis);

            System.out.println(msg.getSubject());
            System.out.println(msg.getSentDate());
            System.out.println(msg.getSender());
            Address[] addresses = msg.getFrom();
            for(Address address: addresses){
                System.out.println(((InternetAddress)address).getAddress());
            }
//            System.out.println(msg.getContentType());

//            Multipart multipart = (Multipart) msg.getContent();
//            Part part = multipart.getBodyPart(0);
//            while(part.isMimeType("multipart/*")){
//                multipart = (Multipart) part.getContent();
//                part = multipart.getBodyPart(0);
//            }
//            System.out.println(part.getContent());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSelect(){
        QueryDto queryDto = new QueryDto();
        queryDto.setKey(null);
        queryDto.setIsContent(0);
        queryDto.setIsAttachment(0);
        List<EmailWithBlobs> list = emailDao.selectByKey(queryDto);
        for(EmailWithBlobs emailWithBlobs: list){
            System.out.println(emailWithBlobs.getTitle());
            System.out.println(emailWithBlobs.getAttachments().size());
        }
    }

    @Test
    public void testTika(){
        File file = new File("D:\\EmailRepo\\简历.docx");
        Tika tika = TikaUtil.getTika();
        try {
//            System.out.println("文件类型:" + tika.detect(file));
//            System.out.println("文件内容:" + tika.parseToString(file));
            Metadata metadata = new Metadata();
            new AutoDetectParser().parse(new FileInputStream(file), new BodyContentHandler(), metadata, new ParseContext());
            String[] names = metadata.names();
            for (String name : names) {
                System.out.println(name + "：" + metadata.get(name));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testKmp(){
        File file = new File("D:\\EmailRepo\\简历.docx");
        Tika tika = TikaUtil.getTika();
        try {
            long start = System.nanoTime();
            String content = tika.parseToString(file).trim();
            String key = "姚欣捷";
            key.indexOf("");
            System.out.println(DetectUtil.detect(key, content));
            long end = System.nanoTime();
            System.out.println((double)(end-start) / 1000000000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFileApi(){
        File file = new File("D:\\EmailRepo\\简历.docx");
        System.out.println(file.getAbsolutePath());
    }

    @Test
    public void findNums(){
        File dir = new File("D:\\EmailRepo\\");
        System.out.println(dir.listFiles().length);
    }

    @Test
    public void testSearch(){
        QueryDto dto = new QueryDto();
        dto.setKey("数据库");
        dto.setIsContent(1);
        dto.setIsAttachment(1);
        EmailState state = emailService.getByKey(dto);
        List<EmailWithBlobs> emailList = state.getEmailList();
        for(EmailWithBlobs email: emailList){
            System.out.println(email.getTitle());
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        EmailState state1 = emailService.getByAttachment();
        List<EmailWithBlobs> emailList2 = state1.getEmailList();
        for(EmailWithBlobs email: emailList2){
            System.out.println(email.getTitle());
        }
    }

    @Test
    public void testParseHtml() {
        String html = "马云的个人信息，马云的简介。";
        System.out.println(HtmlUtil.html2Str(html));
    }
}
