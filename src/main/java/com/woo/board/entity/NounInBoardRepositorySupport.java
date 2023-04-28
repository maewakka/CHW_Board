package com.woo.board.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import static com.woo.board.entity.QNounInBoard.nounInBoard;

@Repository
public class NounInBoardRepositorySupport extends QuerydslRepositorySupport {
    private final JPAQueryFactory jpaQueryFactory;

    public NounInBoardRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(NounInBoard.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<Noun> getNouns(Board board) {
        return jpaQueryFactory.select(nounInBoard.noun).from(nounInBoard).where(nounInBoard.board.eq(board)).fetch();
    }
}
