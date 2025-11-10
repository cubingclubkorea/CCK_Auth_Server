package com.cubingclub.auth.Cubingclub_SSO_Server.Config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> defaultAuthorities = defaultConverter.convert(jwt);

        List<String> roles = jwt.getClaimAsStringList("roles");
        Collection<GrantedAuthority> roleAuthorities = Collections.emptyList();
        if (roles != null) {
            roleAuthorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        return Stream.concat(defaultAuthorities.stream(), roleAuthorities.stream())
                .collect(Collectors.toList());
    }
}
