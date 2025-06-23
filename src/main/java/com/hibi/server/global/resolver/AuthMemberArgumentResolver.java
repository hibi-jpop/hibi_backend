package com.hibi.server.global.resolver;

import com.hibi.server.domain.auth.dto.CustomUserDetails;
import com.hibi.server.global.annotation.AuthMember;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

    //TODO : Oauth 적용 후 Adpter 패턴으로 변경하기
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthMemberAnnotation = parameter.hasParameterAnnotation(AuthMember.class);
        boolean isCustomUserDetailsType = CustomUserDetails.class.isAssignableFrom(parameter.getParameterType());
        return hasAuthMemberAnnotation && isCustomUserDetailsType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("접근 권한이 없습니다. 로그인 후 이용해주세요.");
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) { // 캐스팅할 타입에 맞게 변경
            throw new AccessDeniedException("잘못된 인증 주체 정보입니다.");
        }

        return authentication.getPrincipal();
    }
}
