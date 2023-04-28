package com.woo.board.service;

import com.woo.board.dto.BoardRegistrationDto;
import com.woo.board.dto.RelatedBoard;
import com.woo.board.entity.*;
import com.woo.board.util.WordAnalysis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final WordAnalysis wordAnalysis;
    private final NounRepository nounRepository;
    private final NounInBoardRepository nounInBoardRepository;
    private final NounInBoardRepositorySupport nounInBoardRepositorySupport;

    /**
     * 게시글을 저장하는 메서드
     */
    @Transactional
    public Long BoardRegistration(BoardRegistrationDto requestDto) {
        String title = requestDto.getTitle();
        String content = requestDto.getContent();

        // 제목과 내용이 존재할 경우
        if(StringUtils.hasText(title) && StringUtils.hasText(content)) {
            Board board = Board.builder()
                    .title(title)
                    .content(content)
                    .createdDate(LocalDateTime.now())
                    .build();

            // DB에 게시글을 저장한다.
            boardRepository.save(board);

            // 단어분석 컴포넌트에서 분석한 단어들을 바탕으로
            HashSet<String> nouns = wordAnalysis.doWordNouns(content);
            for(String noun: nouns) {
                Noun saveNoun;
                // 단어와 빈도수를 저장 및 갱신한다.
                if(!nounRepository.existsByNoun(noun)) {
                    saveNoun = Noun.builder()
                            .noun(noun)
                            .num(Long.valueOf(1))
                            .build();
                    nounRepository.save(saveNoun);
                } else {
                    saveNoun = nounRepository.findByNoun(noun).orElseThrow(() -> new EntityNotFoundException("에러가 발생하였습니다."));
                    saveNoun.setNum(saveNoun.getNum()+1);
                }

                // 게시글에 나온 단어들의 목록을 모두 저장한다.
                nounInBoardRepository.save(NounInBoard.builder()
                        .noun(saveNoun)
                        .board(board)
                        .build());
            }
            return board.getId();
        } else {
            throw new RuntimeException("제목 또는 내용을 채워주세요.");
        }
    }

    /**
     * 페이징 처리를 통해 게시글 목록을 반환하는 메서드
     */
    @Transactional
    public Page<Board> GetBoardList(int pageNo, int size) {
        PageRequest pageRequest = PageRequest.of(pageNo, size);

        return boardRepository.findAll(pageRequest);
    }

    /**
     * 게시글의 총 갯수를 반환하는 메서드
     */
    @Transactional
    public Long GetBoardNum() {
        return boardRepository.count();
    }

    /**
     * 게시글 내용을 반환하는 메서드
     */
    @Transactional
    public Board GetBoard(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));
    }

    /**
     * 연관 게시글 목록을 반환하는 메서드
     */
    @Transactional
    public List<RelatedBoard> GetRelatedBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));
        List<Board> boardList = boardRepository.findAll();
        Long boardNum = this.GetBoardNum();
        List<RelatedBoard> relatedBoardList = new ArrayList<>();

        // 해당 게시글이 가지는 단어 목록들을 가져온다.
        List<Noun> nouns = nounInBoardRepositorySupport.getNouns(board);
        List<Noun> thisNouns = new ArrayList<>();

        // 그 단어 목록 중, 전체 게시글 중 60% 이상 나온 단어들은 제외한다.
        for(Noun noun: nouns) {
            if((noun.getNum() / (double) boardNum) < 0.6) {
                thisNouns.add(noun);
            }
        }

        // 모든 게시글 중 연관되어있는지를 판별한다.
        for(Board savedBoard: boardList) {
            if(!savedBoard.getId().equals(id)) {
                int relateNum = 0;

                // 게시글의 단어 목록을 가져온다.
                List<Noun> boardNouns = nounInBoardRepositorySupport.getNouns(savedBoard);
                int totalNum = boardNouns.size();
                // 해당 게시글과 연관 게시글이 가지는 단어의 교집합을 구한다.
                boardNouns.retainAll(thisNouns);

                relateNum = boardNouns.size();
                // 교집합의 크기가 2 이상이면 연관된 게시글이라 판단하여 결과에 추가한다.
                if(relateNum >= 2) {
                    relatedBoardList.add(RelatedBoard.builder()
                            .board(savedBoard)
                            .relate_w((long) relateNum)
                            .total_w((long) totalNum).build());
                }
            }
        }
        // 게시글에 40% 이하 빈도로 나타나는 단어는 자주 나타날수록 더 연관이 있는 것으로 계산하기에, 1차 조건으로 연관된 딘어의 갯수로 정렬한다.
        // 연관게시글에서 40% 이하 빈도로 나타나는 단어중 연관단어가 그렇지 않은 단어보다 더 빈번하게 나타날수록 연관이 더 있는것으로 파악되므로, 해당 게시글의 전체 단어 수에서 연관된 단어의 갯수를 뺀 조건을 2번째로 정렬한다.
        Collections.sort(relatedBoardList, new Comparator<RelatedBoard>() {
            @Override
            public int compare(RelatedBoard o1, RelatedBoard o2) {
                if(o1.getRelate_w() != o2.getRelate_w()) {
                    return (int) (o2.getRelate_w() - o1.getRelate_w());
                } else {
                    return -Long.compare(o1.getTotal_w()-o1.getRelate_w(), o2.getTotal_w()-o2.getTotal_w());
                }
            }
        });

        return relatedBoardList;
    }

}
