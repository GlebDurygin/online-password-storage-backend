package com.university.diploma.session;

import com.university.diploma.entity.User;

/**
 * Session class
 */
public class AppSession {

    /**
     * Authorization timeout = 2 seconds.
     */
    public static final long AUTHORIZATION_TIMEOUT = 2000;
    /**
     * Session timeout = 5 minutes.
     */
    public static final long SESSION_TIMEOUT = 300000;

    protected final User user;
    protected String sessionKey;
    protected String sessionId;
    protected long lastActionTime;
    protected AuthorizationDetails authorizationDetails;

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

    public AuthorizationDetails getAuthorizationDetails() {
        return authorizationDetails;
    }

    public void setAuthorizationDetails(AuthorizationDetails authorizationDetails) {
        this.authorizationDetails = authorizationDetails;
    }

    public boolean checkAuthorizationTimeout() {
        return System.currentTimeMillis() > (lastActionTime + AUTHORIZATION_TIMEOUT);
    }

    public boolean checkSessionTimeout() {
        return System.currentTimeMillis() > (lastActionTime + SESSION_TIMEOUT);
    }

    public void updateLastActionTime() {
        this.lastActionTime = System.currentTimeMillis();
    }
}
