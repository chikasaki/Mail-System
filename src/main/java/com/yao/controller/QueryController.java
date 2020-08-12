package com.yao.controller;

import com.yao.dto.EmailState;
import com.yao.dto.JsonData;
import com.yao.dto.QueryDto;
import com.yao.entity.EmailWithBlobs;
import com.yao.enums.EmailStateEnum;
import com.yao.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/query")
@Slf4j
public class QueryController {

    @Autowired
    private EmailService emailService;

    @RequestMapping("/searchKey")
    @ResponseBody
    public Map searchKey(HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();

        try {
            String key = request.getParameter("key");
            if(key.equals("null")) key = null;
            //是否查询正文
            int isContent = Integer.parseInt(request.getParameter("isContent"));
            //是否查询附件
            int isAttachment = Integer.parseInt(request.getParameter("isAttachment"));

            QueryDto queryDto = new QueryDto();
            queryDto.setKey(key);
            queryDto.setIsContent(isContent);
            queryDto.setIsAttachment(isAttachment);

            EmailState state = emailService.getByKey(queryDto);
            if(state.getState() == EmailStateEnum.FAIL.getState()) throw new RuntimeException(state.getStateInfo());
            map.put("success", true);
            map.put("emailList", state.getEmailList());
            map.put("count", state.getCount());

            return map;
        }catch(Exception e){
            log.error(e.getMessage());
            map.put("success", false);
            map.put("errMsg", e.getMessage());
            return map;
        }
    }

    @RequestMapping("/searchAttachment")
    @ResponseBody
    public Map searchAttachment(){
        Map<String, Object> map = new HashMap<>();
        try{
            EmailState state = emailService.getByAttachment();
            if(state.getState() == EmailStateEnum.SUCCESS.getState()){
                map.put("success", true);
                map.put("emailList", state.getEmailList());
                return map;
            }else{
                throw new RuntimeException("error");
            }
        }catch(Exception e){
            map.put("success", false);
            map.put("errMsg", e.getMessage());
            return map;
        }
    }

    @RequestMapping("/emailInfo")
    @ResponseBody
    public Map getEmailInfo(HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();

        try {
            int emailId = Integer.parseInt(request.getParameter("emailId"));
            EmailState state = emailService.getById(emailId);
            if (state.getState() == EmailStateEnum.FAIL.getState()){
                throw new RuntimeException(state.getStateInfo());
            }

            EmailWithBlobs email = state.getEmail();
            map.put("success", true);
            map.put("email", email);
            return map;
        } catch (Exception e){
            log.error(e.getMessage());
            map.put("success", false);
            map.put("errMsg", e.getMessage());
            return map;
        }
    }
}
