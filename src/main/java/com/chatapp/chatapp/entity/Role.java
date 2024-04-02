package com.chatapp.chatapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {

    private Long id;

    private String name;

    public Role(String name) {
        this.name = name;
    }
}