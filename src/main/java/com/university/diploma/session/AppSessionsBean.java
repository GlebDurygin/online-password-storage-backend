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

    public static final String AUTHENTICATION_SESSION_ID_COOKIE = "AUTHENTICATION_SESSION_ID";
    public static final String SESSION_ID_COOKIE = "SESSION_ID";
    public static final String ANONYMOUS_SESSION_ID = "473dc69678d1c1db737484948eff81a75882fcdfe16ecae83e3fc2e88d6f7034";

    @Autowired
    protected RandomGeneratorService randomGeneratorService;

    private List<AppSession> sessions = new ArrayList<>();

    public AppSession createAppSession(@NonNull User user) {
        AppSession oldAppSession = getAppSessionByUser(user);
        if (oldAppSession != null) {
            removeAppSession(oldAppSession);
        }

        AppSession appSession = new AppSession(user);
        AuthenticationDetails details = createAuthenticationDetails(user);
        appSession.setAuthenticationDetails(details);
        sessions.add(appSession);
        return appSession;
    }

    public AppSession getAppSessionByAuthenticationKey(String authenticationKey) {
        AppSession session = sessions.stream()
                .filter(appSession -> appSession.getAuthenticationDetails() != null
                        && Objects.equals(appSession.getAuthenticationDetails().getAuthenticationKey(), authenticationKey))
                .findFirst()
                .orElse(null);

        if (session != null) {
            if (!session.checkAuthenticationTimeout()) {
                session.updateLastActionTime();
            } else {
                removeAppSession(session);
                session = null;
            }
        }

        return session;
    }

    public AppSession getAppSessionBySessionId(String sessionId) {
        AppSession session = sessions.stream()
                .filter(appSession -> appSession.getSessionId() != null && appSession.getSessionId().equals(sessionId))
                .findFirst()
                .orElse(null);

        return checkSessionTimeout(session);
    }

    protected AuthenticationDetails createAuthenticationDetails(User user) {
        byte[] authenticationKeyBytes = new byte[32];
        randomGeneratorService.nextBytes(authenticationKeyBytes);
        String authenticationKey = new BigInteger(authenticationKeyBytes).toString(16);

        AuthenticationDetails authenticationDetails = new AuthenticationDetails(authenticationKey);

        authenticationDetails.setSalt(user.getSalt());
        authenticationDetails.setVerifier(user.getVerifier());
        return authenticationDetails;
    }

    protected AppSession getAppSessionByUser(User user) {
        AppSession session = sessions.stream()
                .filter(appSession -> Objects.equals(user.getUsername(), appSession.getUser().getUsername()))
                .findFirst()
                .orElse(null);

        return checkSessionTimeout(session);
    }

    protected void removeAppSession(AppSession appSession) {
        sessions.remove(appSession);
    }

    protected AppSession checkSessionTimeout(AppSession appSession) {
        if (appSession != null) {
            if (!appSession.checkSessionTimeout()) {
                appSession.updateLastActionTime();
            } else {
                removeAppSession(appSession);
                appSession = null;
            }
        }

        return appSession;
    }
}
