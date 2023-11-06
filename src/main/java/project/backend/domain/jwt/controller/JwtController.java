package project.backend.domain.jwt.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.backend.domain.jwt.dto.JwtRequestDto;
import project.backend.domain.jwt.dto.KakaoUserInfo;
import project.backend.domain.jwt.response.JwtResponse;
import project.backend.domain.jwt.response.TokenResponse;
import project.backend.domain.jwt.service.JwtService;
import project.backend.domain.member.dto.MemberPatchRequestDto;
import project.backend.domain.member.dto.MemberResponseDto;
import project.backend.domain.member.entity.Member;
import project.backend.domain.member.mapper.MemberMapper;
import project.backend.domain.member.service.MemberService;
import project.backend.global.error.exception.BusinessException;
import project.backend.global.error.exception.ErrorCode;
import project.backend.global.s3.service.ImageService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "로그인 API")
@Validated
@AllArgsConstructor
@Slf4j
public class JwtController {

    private final MemberService memberService;
    private final JwtService jwtService;
    private final MemberMapper memberMapper;
    private final ImageService imageService;


    @ApiOperation(
            value = "회원가입 & 로그인",
            notes = "")
    @PostMapping("/login")
    public ResponseEntity login(
            @Valid @RequestPart JwtRequestDto request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "categorys", required = false) List<String> categorys) {

        // 닉네임 중복 검사
        memberService.verifiedNickname(request.nickname);

        // socialId, socialType기준 Member 반환, 없다면 새로 생성
        Member member = memberService.getMemberBySocial(request.socialId, request.socialType);

        // profile Url 설정
        if (profileImage != null) {
            request.setProfileUrl(imageService.updateImage(profileImage, "Member", "profileUrl"));
        }

        // nickname, profileUrl, marketingAgree, pushAgree 설정
        memberService.patchMember(member.getId(), MemberPatchRequestDto.builder()
                                                                        .nickname(request.nickname)
                                                                        .profileUrl(request.profileUrl)
                                                                        .marketingAgree(request.marketingAgree)
                                                                        .pushAgree(request.pushAgree).build());

        // 온보딩 카테고리 설정
        if (categorys != null) {
            categorys = categorys.stream().distinct().collect(Collectors.toList());
            memberService.onboardingMember(member.id, categorys);
        }

        // accessToken과 refreshToken 발급
        String accessToken = jwtService.getAccessToken(member);
        String refreshToken = member.getRefreshToken();

        // 응답
        MemberResponseDto memberResponse = memberMapper.memberToMemberResponseDto(member);
        memberResponse.setCategorys(member.getOnboardingMemberCategories().stream().map(c -> c.getCategory().getName()).collect(Collectors.toList()));
        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
        JwtResponse jwtResponse = JwtResponse.builder()
                .token(tokenResponse)
                .member(memberResponse).build();
        return new ResponseEntity<>(jwtResponse, HttpStatus.CREATED);
    }
}