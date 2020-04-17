package com.university.diploma.session;

import com.university.diploma.entity.User;

/**
 * Session class
 */
public class AppSession {

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
}
