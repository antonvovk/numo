package com.numo.server.configuration;

import com.numo.proto.AuthenticationServiceGrpc;
import com.numo.proto.UserServiceGrpc;
import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.CompositeGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.check.AccessPredicate;
import net.devh.boot.grpc.server.security.check.AccessPredicateVoter;
import net.devh.boot.grpc.server.security.check.GrpcSecurityMetadataSource;
import net.devh.boot.grpc.server.security.check.ManualGrpcSecurityMetadataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityConfiguration {

    @Bean
    GrpcSecurityMetadataSource grpcSecurityMetadataSource() {
        final ManualGrpcSecurityMetadataSource source = new ManualGrpcSecurityMetadataSource();
        source.set(AuthenticationServiceGrpc.getSignUpMethod(), AccessPredicate.permitAll());
        source.set(AuthenticationServiceGrpc.getVerifyEmailMethod(), AccessPredicate.permitAll());
        source.set(AuthenticationServiceGrpc.getResendConfirmationCodeMethod(), AccessPredicate.permitAll());
        source.set(AuthenticationServiceGrpc.getSignInMethod(), AccessPredicate.permitAll());
        source.set(AuthenticationServiceGrpc.getForgotPasswordMethod(), AccessPredicate.permitAll());
        source.set(AuthenticationServiceGrpc.getConfirmForgotPasswordMethod(), AccessPredicate.permitAll());
        source.set(AuthenticationServiceGrpc.getChangePasswordMethod(), AccessPredicate.fullyAuthenticated());
        source.set(AuthenticationServiceGrpc.getConfirmChangePasswordMethod(), AccessPredicate.fullyAuthenticated());
        source.set(UserServiceGrpc.getGetUserMethod(), AccessPredicate.fullyAuthenticated());
        source.set(UserServiceGrpc.getUpdateUserMethod(), AccessPredicate.fullyAuthenticated());
        source.setDefault(AccessPredicate.denyAll());
        return source;
    }

    @Bean
    GrpcAuthenticationReader authenticationReader() {
        final List<GrpcAuthenticationReader> readers = new ArrayList<>();
        readers.add(new BearerAuthenticationReader(BearerTokenAuthenticationToken::new));
        return new CompositeGrpcAuthenticationReader(readers);
    }

    @Bean
    AccessDecisionManager accessDecisionManager() {
        final List<AccessDecisionVoter<?>> voters = new ArrayList<>();
        voters.add(new AccessPredicateVoter());
        return new UnanimousBased(voters);
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }
}
