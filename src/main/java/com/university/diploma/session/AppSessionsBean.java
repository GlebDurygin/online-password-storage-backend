package com.university.diploma.session;

import com.university.diploma.entity.User;
import com.university.diploma.service.RandomGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The main bean for work with {@link AppSession}
 */
@Component
@Scope("singleton")
public class AppSessionsBean {

    public static final String SESSION_KEY_COOKIE = "SESSION_KEY";
    public static final String ANONYMOUS_SESSION_KEY = "3026f7bbfa68b1ac22be3d719827a5aa2e5e5c599852fd2b9a1123ecfa29b275";

    @Autowired
    protected RandomGeneratorService randomGeneratorService;

    private List<AppSession> sessions = new ArrayList<>();

    public AppSession createAppSession(@NonNull User user) {
        AppSession oldAppSession = getAppSessionByUser(user);
        if (oldAppSession != null) {
            removeAppSession(oldAppSession);
        }

        AppSession appSession = new AppSession(user);
        AuthorizationDetails details = createAuthorizationDetails(user);
        appSession.setAuthorizationDetails(details);
        sessions.add(appSession);
        return appSession;
    }

    public AppSession getAppSessionByAuthorizationKey(String authorizationKey) {
        return sessions.stream()
                .filter(appSession -> appSession.getAuthorizationDetails() != null
                        && Objects.equals(appSession.getAuthorizationDetails().getAuthorizationKey(), authorizationKey))
                .findFirst()
                .orElse(null);
    }

    public AppSession getAppSessionBySessionKey(String sessionKey) {
        return sessions.stream()
                .filter(appSession -> appSession.getSessionKey() != null && appSession.getSessionKey().equals(sessionKey))
                .findFirst()
                .orElse(null);
    }

    protected AuthorizationDetails createAuthorizationDetails(User user) {
        byte[] authorizationKeyBytes = new byte[32];
        randomGeneratorService.nextBytes(authorizationKeyBytes);
        String authorizationKey = new BigInteger(authorizationKeyBytes).toString(16);

        AuthorizationDetails authorizationDetails = new AuthorizationDetails(authorizationKey);

        authorizationDetails.setSalt(user.getSalt());
        authorizationDetails.setVerifier(user.getVerifier());
        return authorizationDetails;
    }

    protected AppSession getAppSessionByUser(User user) {
        return sessions.stream()
                .filter(appSession -> Objects.equals(user.getUsername(), appSession.getUser().getUsername()))
                .findFirst()
                .orElse(null);
    }

    protected void removeAppSession(AppSession appSession) {
        sessions.remove(appSession);
    }
}
