package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    EntityManager em;
    @DisplayName("회원 가입")
    @Test
    void 회원가입 () throws Exception {
        // given
        Member member = new Member();
        member.setUsername("Moya");
        // when
        Long saveId =  memberService.join(member);
        // then
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @DisplayName("중복된 회원 검증")
    @Test
    void 중복_회원_검증 () throws Exception {
        // given
        Member member1 = new Member();
        member1.setUsername("Moya");
        Member member2 = new Member();
        member2.setUsername("Moya");
        // when
        memberService.join(member1);
        // then
        assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        }, "이미 존재하는 회원입니다.");
    }
}