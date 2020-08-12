package com.yao.dao;

import com.yao.dto.QueryDto;
import com.yao.entity.EmailWithBlobs;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailDao {

    int insert(EmailWithBlobs email);
    List<EmailWithBlobs> selectByKey(QueryDto queryDto);
    EmailWithBlobs selectById(int id);
}
