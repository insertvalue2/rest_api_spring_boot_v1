package com.newtenco.blog.admin;

import com.newtenco.blog.board.Board;
import com.newtenco.blog.board.BoardRepository;
import com.newtenco.blog.core.handler.errors.NotFoundException;
import com.newtenco.blog.user.User;
import com.newtenco.blog.user.UserRepository;
import com.newtenco.blog.user.UserRequest;
import com.newtenco.blog.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public void 관리자_게시글삭제(Integer id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));
        boardRepository.delete(board);
    }

    @Transactional
    public UserResponse.RolesDTO 관리자_역할수정(Integer id, UserRequest.RolesDTO reqDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다"));
        user.updateRoles(reqDTO.roles());
        return new UserResponse.RolesDTO(user);
    }
}
