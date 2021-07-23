package com.example.securitytest.controller;

import com.example.securitytest.config.auth.PrincipalDetails;
import com.example.securitytest.model.User;
import com.example.securitytest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class ApiController {

    final private UserRepository userRepository;
    final private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    @ResponseBody
    public String loginTest(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println("/test/login ============");
        System.out.println("authentication: " + authentication.getPrincipal());
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();   // 구글에서는 오류가 남! classCastException

        System.out.println("principalDetails: " + principalDetails.getUser());
        System.out.println("userDetails: " + userDetails.getUsername());
        return "session check";
    }


    @GetMapping("/test/oauth/login")
    @ResponseBody
    public String loginOauthTest(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oAuth2
    ) {
        System.out.println("/test/oauth/login ============");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication: " + oAuth2User.getAttributes());
        System.out.println("authentication: " + oAuth2.getAttributes());

        return "Oauth session check";
    }


    @GetMapping("/")
    public String index(){
        return "index";
    }

    // OAuth 로그인을 해도
    // 일반 로그인을 해도 PrincipalDetails로 접근해짐.
    @GetMapping("/user")
    @ResponseBody
    public String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails: "+ principalDetails.getUser());
        return "user";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {

        return "manager";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/logout.do")
    public String logout() {
        System.out.println("logout");
        return "redirect:/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
//		System.out.println("joinForm controller start...");
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        System.out.println("join start... , user: "+user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);	// 회원가입 잘됨 but, 시큐리티로 로그인을 할 수 없음. 패스워드 암호화가 안되었기 때문에 => bCryptPasswordEncoder 해야됨!
        return "redirect:/loginForm";
    }

}
