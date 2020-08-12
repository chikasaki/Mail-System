package com.yao.dto;

import com.yao.entity.EmailWithBlobs;
import com.yao.enums.EmailStateEnum;
import com.yao.service.EmailService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmailState {

    private int state;
    private String stateInfo;
    private EmailWithBlobs email;
    private List<EmailWithBlobs> emailList;
    private int count;

    public EmailState(EmailStateEnum enums){
        this.state = enums.getState();
        this.stateInfo = enums.getStateInfo();
    }

    public EmailState(EmailStateEnum enums, List<EmailWithBlobs> emailList){
        this.state = enums.getState();
        this.stateInfo = enums.getStateInfo();
        this.emailList = emailList;
    }

    public EmailState(EmailStateEnum enums, EmailWithBlobs email){
        this.state = enums.getState();
        this.stateInfo = enums.getStateInfo();
        this.email = email;
    }
}
