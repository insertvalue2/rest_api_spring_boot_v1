package com.newtenco.blog.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    @Query(value = "select b from Board b join fetch b.user where b.title like concat('%', :keyword, '%')",
            countQuery = "select count(b) from Board b where b.title like concat('%', :keyword, '%')")
    Page<Board> findAllJoinUser(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            select b from Board b
            join fetch b.user
            left join fetch b.replies r
            left join fetch r.user
            where b.id = :id
            order by r.id
            """)
    Optional<Board> findByIdJoinUserAndReplies(@Param("id") Integer id);
}
