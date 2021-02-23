package com.nemanjav.back.jwt;

import com.nemanjav.back.entity.User;
import com.nemanjav.back.enums.ResultEnum;
import com.nemanjav.back.exception.MyException;
import com.nemanjav.back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = getToken(request);
        if(jwtToken != null && jwtProvider.validate(jwtToken)){
            try{

                String userAccount = jwtProvider.getUserAccount(jwtToken);
                User loggedInUser = userRepository.findByEmail(userAccount).get();

                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(loggedInUser.getUserRole().toString());
                ArrayList<SimpleGrantedAuthority> grantedAuthorityArrayList = new ArrayList<>();
                grantedAuthorityArrayList.add(simpleGrantedAuthority);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        loggedInUser.getEmail() , null ,grantedAuthorityArrayList);
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }catch(Exception e){
                    throw new MyException(ResultEnum.SET_AUTH_FROM_JWT_FAIL);
            }
        }
        filterChain.doFilter(request , response);
    }

    private String getToken(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            return authHeader.replace("Bearer " , "");
        }

        return null;
    }
}
