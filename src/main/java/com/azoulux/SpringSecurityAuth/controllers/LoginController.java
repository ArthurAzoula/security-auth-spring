package com.azoulux.SpringSecurityAuth.controllers;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class LoginController {

    private OAuth2AuthorizedClientService authorizedClient;

    public LoginController(OAuth2AuthorizedClientService authorizedClient) {
        this.authorizedClient = authorizedClient;
    }

    @GetMapping("/user")
    public String getUser() {
        return "Welcome User!";
    }

    @GetMapping("/admin")
    public String getAdmin() {
        return "Welcome Admin!";
    }

    @GetMapping("/")
    public String getUserInfo(Principal user, @AuthenticationPrincipal OidcUser oidcUser) {
        StringBuffer userInfo = new StringBuffer();
        if (user instanceof UsernamePasswordAuthenticationToken) {
            userInfo.append(getUsernamePasswordLoginInfo(user));
        } else if (user instanceof OAuth2AuthenticationToken) {
            userInfo.append(getOAuth2LoginInfo(user, oidcUser));
        } else {
            userInfo.append("NA");
        }
        return userInfo.toString();
    }

    private StringBuffer getUsernamePasswordLoginInfo(Principal user) {
        StringBuffer usernameInfo = new StringBuffer();
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) user;
        if (token.isAuthenticated()) {
            User u = (User) token.getPrincipal();
            usernameInfo.append("Welcome " + u.getUsername() + "!");
        } else {
            usernameInfo.append("NA");
        }
        return usernameInfo;
    }

    private StringBuffer getOAuth2LoginInfo(Principal user, OidcUser oidcUser) {
        StringBuffer protectedInfo = new StringBuffer();
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) user;
        OAuth2AuthorizedClient authClient = authorizedClient.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(), token.getName());
        if(token.isAuthenticated()) {
            Map<String, Object> userAttributes = ((DefaultOAuth2User) token.getPrincipal()).getAttributes();
            String userToken = authClient.getAccessToken().getTokenValue();
            protectedInfo.append("Welcome, ").append(userAttributes.get("name")).append("<br><br>");
            protectedInfo.append("email : ").append(userAttributes.get("email")).append("<br><br>");
            protectedInfo.append("Access token :").append(userToken);

            OidcIdToken idToken = oidcUser.getIdToken();
            if (idToken != null) {
                protectedInfo.append("<br><br>Token Value : ").append(idToken.getTokenValue()).append("<br><br>");
                protectedInfo.append("Token mapped value : ").append("<br><br>");
                Map<String, Object> claims = idToken.getClaims();
                for (String key: claims.keySet()) {
                    protectedInfo.append(key).append(" : ").append(claims.get(key)).append("<br>");
                }
            }
        } else {
            protectedInfo.append("NA");
        }
        return protectedInfo;

    }
}
