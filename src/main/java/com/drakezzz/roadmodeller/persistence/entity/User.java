package com.drakezzz.roadmodeller.persistence.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String middleName;

    private LocalDateTime createdAt;

    private Boolean isBlocked;

}
