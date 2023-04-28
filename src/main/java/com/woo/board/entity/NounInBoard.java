package com.woo.board.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
public class NounInBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noun_in_board_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "noun")
    private Noun noun;

    @Builder
    public NounInBoard(Board board, Noun noun) {
        this.board = board;
        this.noun = noun;
    }
}
