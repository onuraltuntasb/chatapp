package com.chatapp.chatapp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Privilege {

    private Long id;

    private String name;

    public Privilege(String name) {
        this.name = name;
    }
}
