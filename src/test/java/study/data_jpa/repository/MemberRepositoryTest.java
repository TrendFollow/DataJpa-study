package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    EntityManager em;

    @Test
    public void testMember() throws Exception{
        // given
        Member member = new Member("memberB");
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(savedMember.getId()).orElse(null);

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() throws Exception{
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        Member findMember1 = memberRepository.findById(member1.getId()).orElse(null);
        Member findMember2 = memberRepository.findById(member2.getId()).orElse(null);

        // then
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() throws Exception{
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findTop3HelloBy() throws Exception{
        // given
        List<Member> top3HelloBy = memberRepository.findTop3HelloBy();
        // when

        // then
    }

    @Test
    public void findByUsername() throws Exception{
        // given
        Member m1 = new Member("member1");
        Member m2 = new Member("member2");
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findByUsername("member1");

        // then
        assertThat(result.get(0).getUsername()).isEqualTo(m1.getUsername());
    }

    @Test
    public void findUser() throws Exception{
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findUser("AAA", 10);

        // then
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() throws Exception{
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println(s);
        }
        // then
    }

    @Test
    public void findMemberDto() throws Exception{
        // given
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        // when
        List<MemberDto> memberDtos = memberRepository.findMemberDto();
        for (MemberDto memberDto : memberDtos) {
            System.out.println(memberDto);
        }
        // then
    }

    @Test
    public void findByNames() throws Exception{
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "abc"));
        for (Member member : result) {
            System.out.println(member);
        }
        // then
    }

    @Test
    public void returnType() throws Exception{
        // given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
//        Member m3 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
//        memberRepository.save(m3);

        // when
        List<Member> aaa = memberRepository.findListByUsername("abcd");
        Member aaa1 = memberRepository.findMemberByUsername("AAAaaa");
        // 단건 조회시 값이 없으면 optional.empty로 반환하지만 2개 이상이면 에러 터진다.
        Optional<Member> aaa2 = memberRepository.findOptionalByUsername("AAA");

        System.out.println(aaa.size());
        System.out.println(aaa1);
        System.out.println(aaa2);
        // then
    }

    @Test
    public void paging() throws Exception{
        // given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        // then
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); // 페이지 갯수
        assertThat(page.isFirst()).isTrue(); // 첫번째 페이지인지
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는지
    }

    @Test
    public void slice() throws Exception{
        // given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);
        List<Member> content = page.getContent();

        // then
        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
//        assertThat(page.getTotalPages()).isEqualTo(2); // 페이지 갯수
        assertThat(page.isFirst()).isTrue(); // 첫번째 페이지인지
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는지
    }

    @Test
    public void bulkAgePlus() throws Exception{
        // given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.clear();


        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println(member5.getAge());

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception{
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        // LAZY 로 설정되어 있기 때문에 team을 가져올 때 프록시 객체로 가져온다. 팀 프록시에는 식별자가 있어서 식별자를 가져올 때는 1차 캐시에서 바로 가져올 수 있다.
        // 식별자가 아닌 것을 가져올 때는 각 쿼리를 다시 한 번 날려야 한다.
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        // then
    }

    @Test
    public void findMemberFetchJoin() throws Exception{
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findMemberFetchJoin();
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        // then
    }

    @Test
    public void EntityGraph() throws Exception{
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findEntityGrapthByUsername("member1");
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
        // then
    }

    @Test
    public void queryHint() throws Exception{
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        // findById로 가져오는 순간 원본엔티티, 스냅샷 두개를 관리
        Member findMember = memberRepository.findById(member1.getId()).orElse(null);
        findMember.setUsername("member2");

        em.flush();
        // then
    }

    @Test
    public void queryHintMain() throws Exception{
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        // queryhint readOnly시 update를 하지 않는다.
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");
        em.flush();
        // then
    }

    @Test
    public void lock() throws Exception{
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        List<Member> member11 = memberRepository.findLockByUsername("member1");
        em.flush();
        // then
    }

    @Test
    public void callCustom() throws Exception{
        // given
        List<Member> memberCustom = memberRepository.findMemberCustom();
        // when

        // then
    }

    @Test
    public void specBasic() throws Exception{
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        assertThat(result.size()).isEqualTo(1);

        // then
    }

    @Test
    public void queryExample() throws Exception{
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        // Probe
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUsername()).isEqualTo("m1");

        // then
    }

    @Test
    public void projections() throws Exception{
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections usernameOnly : result) {
            System.out.println("username = " + usernameOnly.getUsername());
            System.out.println("username = " + usernameOnly.getTeam().getName());
        }
        // then
    }

    @Test
    public void findByNativeQuery() throws Exception{
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
//        Member member = memberRepository.findByNativeQuery("m1");
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }

        // then
    }
}