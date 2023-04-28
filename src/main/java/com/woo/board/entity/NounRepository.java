package com.woo.board.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NounRepository extends JpaRepository<Noun, Long> {
    boolean existsByNoun(String noun);
    Optional<Noun> findByNoun(String noun);
}
