package com.yao.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class Email {

    private Integer id;
    private String title;
    private String from;
    private Date sendTime;

    private List<Attachment> attachments;
}
