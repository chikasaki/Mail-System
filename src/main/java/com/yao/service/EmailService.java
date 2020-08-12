package com.yao.service;

import com.yao.dto.EmailState;
import com.yao.dto.JsonData;
import com.yao.dto.QueryDto;
import com.yao.entity.EmailWithBlobs;

import java.util.List;

public interface EmailService {

    EmailState add(EmailWithBlobs email);

    EmailState getByKey(QueryDto queryDto);
    EmailState getByAttachment();
    EmailState getById(int emailId);
}
