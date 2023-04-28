package com.woo.board.controller;

import com.woo.board.dto.BoardRegistrationDto;
import com.woo.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RestController
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/create")
    public ResponseEntity BoardRegistration(@RequestBody BoardRegistrationDto requestDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMsg);
        }
        try {
            return ResponseEntity.ok(Long.toString(boardService.BoardRegistration(requestDto)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/board/list")
    public ResponseEntity GetBoardList(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                       @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        return ResponseEntity.ok(boardService.GetBoardList(pageNo, size));
    }

    @GetMapping("/board/num")
    public ResponseEntity GetBoardNum() {
        return ResponseEntity.ok(boardService.GetBoardNum());
    }

    @GetMapping("board")
    public ResponseEntity GetBoard(@RequestParam(value = "id") Long id) {
        try {
            return ResponseEntity.ok(boardService.GetBoard(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/board/relation")
    public ResponseEntity GetRelatedBoard(@RequestParam(value = "id") Long id) {

        return ResponseEntity.ok(boardService.GetRelatedBoard(id));
    }
}
