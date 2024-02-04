package com.example.app.controller;

import com.example.app.domain.Member;
import com.example.app.repository.MemberRepsotiroy;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final MemberRepsotiroy memberRepsotiroy;

    @GetMapping("/members")
    public List<Member> members() {
        return memberRepsotiroy.findAll();
    }

}
