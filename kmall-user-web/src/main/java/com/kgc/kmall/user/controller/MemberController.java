package com.kgc.kmall.user.controller;

import com.kgc.kmall.bean.Member;
import com.kgc.kmall.service.MemberService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MemberController {
    @Reference
    MemberService memberService;
    @RequestMapping("/")
    public List<Member> selectAllMember(){
        List<Member> members = memberService.selectAllMember();
        return members;
    }
}
