package com.hibi.server.domain.member.repository;

import com.hibi.server.domain.member.entity.Member;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    Optional<Member> findByEmailAndDeletedAtIsNotNull(String email);

    boolean existsByNickname(String nickname);

    boolean existsById(@NonNull Long id);
}
