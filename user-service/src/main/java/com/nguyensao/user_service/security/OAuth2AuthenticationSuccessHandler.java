package com.nguyensao.user_service.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.nguyensao.user_service.model.UserProvider;
import com.nguyensao.user_service.repository.UserProviderRepository;
import com.nguyensao.user_service.repository.UserRepository;
import com.nguyensao.user_service.utils.JwtUtil;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil, UserRepository userRepository,
            UserProviderRepository userProviderRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userProviderRepository = userProviderRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        System.out.println(">>> OAuth2AuthenticationSuccessHandler được gọi");
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        if (email == null) {
            System.err.println(">>> Lỗi: Không thể lấy email từ OAuth2 user");
            throw new IllegalStateException("Không thể lấy email từ OAuth2 user");
        }
        // Kiểm tra token trong cookie
        String token = null;
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (cookie.getName().equals("_tk")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        // Nếu có token → kiểm tra liên kết
        if (token != null) {
            String jwtEmail = jwtUtil.decodedToken(token);
            // Kiểm tra xem liên kết đã tồn tại chưa
            var userOpt = userRepository.findByEmailWithProviders(jwtEmail);
            if (userOpt.isPresent()) {
                var user = userOpt.get();
                String provider = oauth2User.getAttribute("iss") != null ? "GOOGLE" : "FACEBOOK";
                boolean alreadyLinked = false;
                for (UserProvider up : user.getProviders()) {
                    if (up.getProvider().equals(provider)) {
                        if (!up.isActive()) {
                            up.setActive(true);
                            userProviderRepository.save(up);
                            alreadyLinked = false;
                            break;
                        }
                        alreadyLinked = true;
                    } else {
                        alreadyLinked = true;
                        break;
                    }

                }

                if (alreadyLinked) {
                    System.out.println(">>> Tài khoản đã được liên kết với " + provider);
                    response.sendRedirect("/api/v1/users/linked-already");
                    return;
                } else {
                    System.out.println(">>> Liên kết thành công với " + provider);
                    response.sendRedirect("/api/v1/users/linked-success");
                    return;
                }
            }
        }

        System.out.println(">>> Tạo token cho email: " + email);
        try {
            String accessToken = jwtUtil.decodedToken(email);
            System.out.println(">>> Token được tạo: " + accessToken);
            response.sendRedirect("/api/v1/users/success");
        } catch (Exception e) {
            System.err.println(">>> Lỗi khi tạo token: " + e.getMessage());
            throw new IllegalStateException("Lỗi khi tạo token: " + e.getMessage());
        }
    }

}