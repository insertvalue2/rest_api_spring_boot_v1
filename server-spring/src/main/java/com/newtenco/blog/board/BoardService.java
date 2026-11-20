package com.newtenco.blog.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardResponse.ListDTO> 글목록() {
        return boardRepository.findAllJoinUser().stream()
                .map(BoardResponse.ListDTO::new)
                .toList();
    }
}
