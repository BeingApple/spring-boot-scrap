package com.yongbi.szsyongbi.configuration.security;

import com.yongbi.szsyongbi.member.application.port.out.ReadMemberPort;
import com.yongbi.szsyongbi.security.domain.AppUserDetails;
import com.yongbi.szsyongbi.security.domain.Roles;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {
    private final ReadMemberPort readMemberPort;

    public AppUserDetailsService(ReadMemberPort readMemberPort) {
        this.readMemberPort = readMemberPort;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var member = readMemberPort.read(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new AppUserDetails(member.id(),
                member.userId(),
                member.password(),
                member.name(),
                Roles.ROLE_USER.name());
    }
}
