package com.woo.board.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
public class Noun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noun_id")
    private Long id;

    private String noun;
    private Long num;

    @Builder
    public Noun(String noun, Long num) {
        this.noun = noun;
        this.num = num;
    }
}
