package project.backend.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.backend.domain.category.service.CategoryService;
import project.backend.domain.jwt.dto.JwtRequestDto;
import project.backend.domain.member.dto.MemberPatchRequestDto;
import project.backend.domain.member.dto.MemberPostRequestDto;
import project.backend.domain.member.entity.Agree;
import project.backend.domain.member.entity.SocialType;
import project.backend.domain.member.entity.Member;
import project.backend.domain.member.mapper.MemberMapper;
import project.backend.domain.member.repository.MemberRepository;
import project.backend.domain.onboardingmembercategory.service.OnboardingMemberCategoryService;
import project.backend.global.error.exception.BusinessException;
import project.backend.global.error.exception.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final CategoryService categoryService;
    private final OnboardingMemberCategoryService onboardingMemberCategoryService;


    /**
     * socialId와 socialType 기준 Member 반환
     * @param socialId
     * @param socialType
     * @return Member
     */
    public Member getMemberBySocial(String socialId, SocialType socialType) {
        return memberRepository.findFirstBySocialIdAndSocialType(socialId, socialType)
                .orElseGet(() -> createMember(socialId, socialType));
    }

    /**
     * socialId와 socialType를 가지고 있는 Member 생성
     * @param socialId
     * @param socialType
     * @return Memeber
     */
    public Member createMember(String socialId, SocialType socialType) {
        Member member = Member.builder()
                .socialId(socialId)
                .socialType(socialType)
                .marketingAgree(Agree.DISAGREE)
                .pushAgree(Agree.DISAGREE).build();
        memberRepository.save(member);
        return member;
    }

    /**
     * 닉네임 중복 검사
     * @param nickname
     * @return
     */
    public void verifiedNickname(String nickname) {
        if (nickname != null && memberRepository.findAllByNickname(nickname).size() > 0) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATE);
        }
    }

    public Member getMemberBySocialIdAndSocialType(String socialId, SocialType socialType) {
        return memberRepository.findFirstBySocialIdAndSocialType(socialId, socialType).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getMember(Long id) {
        return verifiedMember(id);
    }

    public List<Member> getMemberList() {
        return memberRepository.findAll();
    }



    public Member patchMember(Long id, MemberPatchRequestDto memberPatchRequestDto) {
        Member member = verifiedMember(id).patchMember(memberPatchRequestDto);
        memberRepository.save(member);
        return member;
    }

    public Member onboardingMember(Long id, List<String> categorys) {
        Member member = verifiedMember(id);
        onboardingMemberCategoryService.deleteOnboardingMemberCategoryByMember(member);
        for(String category : categorys) {
            onboardingMemberCategoryService.createOnboardingMemberCategory(member, categoryService.verifiedCategory(category));
        }
        return member;
    }

    public void deleteMember(Long id) {
        memberRepository.delete(verifiedMember(id));
    }

    public Member verifiedMember(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

}
