package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class ViewController {
    private final RegisteredClientRepository registeredClientRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String joinPage(){ return "signup"; }

    @GetMapping("/password-reset")
    public String passwordPage(){ return "password"; }

    @GetMapping("/consent")
    public String consent(Principal principal, Model model,
                          @RequestParam("client_id") String clientId,
                          @RequestParam("scope") String scope,
                          @RequestParam("state") String state) {

        Set<String> scopesToApprove = new LinkedHashSet<>();
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        Set<String> scopes = Objects.requireNonNull(registeredClient).getScopes();
        for (String requestedScope : StringUtils.delimitedListToStringArray(scope, " ")) {
            if (scopes.contains(requestedScope)) {
                scopesToApprove.add(requestedScope);
            }
        }

        model.addAttribute("clientId", clientId);
        model.addAttribute("clientName", registeredClient.getClientName());
        model.addAttribute("state", state);
        model.addAttribute("scopes", withDescription(scopesToApprove));
        model.addAttribute("principalName", principal.getName());
        model.addAttribute("redirectUri", registeredClient.getRedirectUris().iterator().next());

        return "consent";
    }

    private static Set<ScopeWithDescription> withDescription(Set<String> scopes) {
        Set<ScopeWithDescription> scopeWithDescriptions = new LinkedHashSet<>();
        for (String scope : scopes) {
            scopeWithDescriptions.add(new ScopeWithDescription(scope));

        }
        return scopeWithDescriptions;
    }

    public static class ScopeWithDescription {
        private static final Map<String, String> scopeDescriptions = new HashMap<>();

        static {
            scopeDescriptions.put(
                    "profile",
                    "프로필"
            );
            scopeDescriptions.put(
                    "email",
                    "이메일"
            );
            scopeDescriptions.put(
                    "phone",
                    "전화번호"
            );
        }

        public final String scope;
        public final String description;

        ScopeWithDescription(String scope) {
            this.scope = scope;
            this.description = scopeDescriptions.get(scope);
        }
    }
}
