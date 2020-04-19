package com.university.diploma.session;

import com.university.diploma.entity.User;

/**
 * Session class
 */
public class AppSession {

    /**
     * Authentication timeout = 2 seconds.
     */
    public static final long AUTHENTICATION_TIMEOUT = 20000000;
    /**
     * Session timeout = 5 minutes.
     */
    public static final long SESSION_TIMEOUT = 300000000;

    protected final User user;
    protected String sessionKey;
    protected String sessionId;
    protected long lastActionTime;
    protected AuthenticationDetails authenticationDetails;

    public AppSession(User user) {
        this.user = user;
        this.lastActionTime = System.currentTimeMillis();
    }

    public User getUser() {
        return user;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getLastActionTime() {
        return lastActionTime;
    }

    public void setLastActionTime(long lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    public AuthenticationDetails getAuthenticationDetails() {
        return authenticationDetails;
    }

    public void setAuthenticationDetails(AuthenticationDetails authenticationDetails) {
        this.authenticationDetails = authenticationDetails;
    }

    public boolean checkAuthenticationTimeout() {
        return System.currentTimeMillis() > (lastActionTime + AUTHENTICATION_TIMEOUT);
    }

    public boolean checkSessionTimeout() {
        return System.currentTimeMillis() > (lastActionTime + SESSION_TIMEOUT);
    }

    public void updateLastActionTime() {
        this.lastActionTime = System.currentTimeMillis();
    }
}
