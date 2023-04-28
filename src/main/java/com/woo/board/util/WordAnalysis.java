package com.woo.board.util;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
@Component
public class WordAnalysis {

    Komoran komoran = null;
    StanfordCoreNLP pipeline = null;

    public WordAnalysis() {
        this.komoran = new Komoran(DEFAULT_MODEL.FULL);
    }

    public HashSet<String> doWordNouns(String content) {
        // 단어 빈도를 저장할 HashMap
        HashSet<String> nounResult = new HashSet<>();
        // 단어를 저장할 List
        List<String> nounList = new ArrayList<>();

        // 특수문자와 양옆 공백제거
        content = content.replaceAll("[^가-힣a-zA-Z0-9]", " ").trim();
        // 영어 분리
        String english_content = content.replaceAll("[^A-Za-z]", " ").toLowerCase();
        // Komoran 라이브러리를 활용하여 형태소 분석
        if(StringUtils.hasText(content)) {
            KomoranResult result = komoran.analyze(content);
            nounList.addAll(result.getNouns());
        }
        // Stanford NLP를 활용하여 문장문석
        if(StringUtils.hasText(english_content)) {
            Sentence sent = new Sentence(english_content);
            nounList.addAll(sent.words());
        }

        // 단어의 빈도수를 체크하여 HashMap에 저장
        for(String noun: nounList) {
            nounResult.add(noun);
        }
        return nounResult;
    }
}
