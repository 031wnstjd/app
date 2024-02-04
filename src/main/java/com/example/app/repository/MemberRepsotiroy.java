package com.example.app.repository;

import com.example.app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepsotiroy extends JpaRepository<Member, Long> {
}
