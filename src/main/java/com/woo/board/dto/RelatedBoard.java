package com.woo.board.dto;

import com.woo.board.entity.Board;
import lombok.Builder;
import lombok.Data;

@Data
public class RelatedBoard {

    private Board board;
    private Long relate_w;
    private Long total_w;

    @Builder
    public RelatedBoard(Board board, Long relate_w, Long total_w) {
        this.board = board;
        this.relate_w = relate_w;
        this.total_w = total_w;
    }
}
