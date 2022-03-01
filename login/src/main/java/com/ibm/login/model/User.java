package com.ibm.login.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class User {

    @Id
    private String userId;
    private String firstName;
    private String lastName;
    private String userPassword;
    private String userRole;
    private LocalDateTime userAddedDate;
    private String userToken;
    private String transactionToken;
    private String serviceToken;
}
