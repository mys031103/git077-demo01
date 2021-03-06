package com.kgc.kmall.service;

import com.kgc.kmall.bean.Member;
import com.kgc.kmall.bean.Member_Receive_Address;

import java.util.List;

public interface MemberService {
    List<Member> selectAllMember();

    Member login(Member member);

    void addUserToken(String token, Long memberId);

    Member checkOauthUser(Long sourceUid);

    void addOauthUser(Member umsMember);

    List<Member_Receive_Address> getReceiveAddressByMemberId(Long memberId);

    Member_Receive_Address getReceiveAddressById(Long receiveAddressId);
}
