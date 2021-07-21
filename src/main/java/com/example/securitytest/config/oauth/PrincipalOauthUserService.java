package com.example.securitytest.config.oauth;

import com.example.securitytest.config.auth.PrincipalDetails;
import com.example.securitytest.model.User;
import com.example.securitytest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalOauthUserService extends DefaultOAuth2UserService {

    final private BCryptPasswordEncoder bCryptPasswordEncoder;

    final private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration: "+userRequest.getClientRegistration());
        System.out.println("getAccessToken: "+userRequest.getAccessToken().getTokenValue());
//        System.out.println("getAdditionalParameters: "+userRequest.getAdditionalParameters());
        System.out.println("super.loadUser(userRequest).getAttributes: "+super.loadUser(userRequest).getAttributes());

        // oauth 회원가입 강제 등록
        OAuth2User oAuth2User = super.loadUser(userRequest);

//        System.out.println("getAdditionalParameters: "+userRequest.getAdditionalParameters());
//        System.out.println("getAttributes: "+oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();    // google, facebook
        String providerId = oAuth2User.getAttribute("sub");                // facebook일 때는 null
        String username = provider + "_" + providerId;							// google_1097~~
        String password = bCryptPasswordEncoder.encode("getitthere");
        String email = oAuth2User.getAttribute("email");
        String role = "ROLE_USER";

        var userEntity = userRepository.findByUsername(username);

        if(userEntity == null){
            System.out.println("등록 최초");
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        } else {
            System.out.println("이미 등록되어 있음");
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
