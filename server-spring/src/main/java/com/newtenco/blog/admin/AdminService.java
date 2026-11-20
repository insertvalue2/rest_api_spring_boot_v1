package com.newtenco.blog.admin;

import com.newtenco.blog.board.Board;
import com.newtenco.blog.board.BoardRepository;
import com.newtenco.blog.core.handler.errors.BadRequestException;
import com.newtenco.blog.core.handler.errors.NotFoundException;
import com.newtenco.blog.user.User;
import com.newtenco.blog.user.UserRepository;
import com.newtenco.blog.user.UserRequest;
import com.newtenco.blog.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminService {

    private static final Set<String> ALLOWED_ROLES = Set.of("USER", "ADMIN");

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
        Set<String> requestedRoles = new LinkedHashSet<>(reqDTO.roles());
        if (!ALLOWED_ROLES.containsAll(requestedRoles)) {
            throw new BadRequestException("역할은 USER, ADMIN 만 사용할 수 있습니다");
        }
        if (new HashSet<>(user.getRoles()).equals(requestedRoles)) {
            throw new BadRequestException("현재와 같은 역할입니다");
        }
        user.updateRoles(List.copyOf(requestedRoles));
        return new UserResponse.RolesDTO(user);
    }
}
