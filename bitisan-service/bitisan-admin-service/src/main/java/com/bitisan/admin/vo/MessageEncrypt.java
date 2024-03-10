package com.bitisan.admin.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MessageEncrypt {

    private String message;

    private String password;
}
