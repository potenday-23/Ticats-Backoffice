package project.backend.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.backend.domain.member.entity.Member;
import project.backend.domain.member.entity.SocialType;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findFirstBySocialId(String socialId);
    Optional<Member> findFirstBySocialIdAndSocialType(String socialId, SocialType socialType);
}
