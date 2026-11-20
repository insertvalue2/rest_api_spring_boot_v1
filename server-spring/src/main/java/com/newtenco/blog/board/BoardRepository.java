package com.newtenco.blog.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    @Query(value = "select b from Board b join fetch b.user",
            countQuery = "select count(b) from Board b")
    Page<Board> findAllJoinUser(Pageable pageable);

    @Query("select b from Board b join fetch b.user where b.id = :id")
    Optional<Board> findByIdJoinUser(@Param("id") Integer id);
}
