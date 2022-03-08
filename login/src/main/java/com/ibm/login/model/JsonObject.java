package com.ibm.login.model;

import lombok.Data;

@Data
public class JsonObject {

    private String sub;
    private String iat;
    private String exp;
}
