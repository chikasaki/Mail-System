package com.yao.dao;

import com.yao.entity.Attachment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("attachmentDao")
public interface AttachmentDao {

    int batchInsert(@Param("attachments") List<Attachment> attachments);
    List<Attachment> selectByEmailId(int emailId);
    List<Attachment> selectAll();
}
