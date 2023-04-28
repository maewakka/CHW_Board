package com.woo.board.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BoardRegistrationDto {

    @NotBlank(message = "빈 항목이 존재합니다.")
    private String title;
    @NotBlank(message = "빈 항목이 존재합니다.")
    private String content;

    @Builder
    public BoardRegistrationDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
