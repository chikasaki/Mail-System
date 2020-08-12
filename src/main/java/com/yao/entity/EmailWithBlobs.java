package com.yao.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EmailWithBlobs extends Email{

    private String content;
}
