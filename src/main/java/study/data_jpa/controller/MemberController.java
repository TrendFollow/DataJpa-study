package study.data_jpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.MemberRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).orElse(null);
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }

//    http://localhost:8081/members?page=0&size=20&sort=username,desc
//    page = 현재 페이지, size = 기본 20, sort 분류 순서
//    yml에 글로벌 설정을 넣을 수 있다.
//    @PageableDefault 특별 설정을 넣을 수도 있다.
    @GetMapping("/members")
    public Page<MemberDto> list(/* @PageableDefault(size = 5) */ Pageable pageable){
        Page<Member> page = memberRepository.findAll(pageable);
//        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        Page<MemberDto> map = page.map(MemberDto::new);
        return map;
    }

    @GetMapping("/member")
    public List<Member> list(){
        return memberRepository.findAll();
    }

    @PatchMapping("/member/{id}")
    @Transactional
    public Member create(@PathVariable("id") Long id, @RequestBody Member member){
        Member findMember = memberRepository.findById(id).orElse(null);

        if(member.getUsername() != null){
            findMember.setUsername(member.getUsername());
        }

        if(findMember.getAge() != member.getAge()){
            findMember.setAge(member.getAge());
        }


        return findMember;
    }

//    @PostConstruct
    public void init(){
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
