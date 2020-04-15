package com.university.diploma.controller;

import com.university.diploma.entity.User;
import com.university.diploma.form.RecordClientForm;
import com.university.diploma.form.RecordForm;
import com.university.diploma.form.UserIdForm;
import com.university.diploma.form.UserProfileForm;
import com.university.diploma.service.RecordDataService;
import com.university.diploma.service.UserDataService;
import com.university.diploma.session.AppSession;
import com.university.diploma.session.AppSessionsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.university.diploma.session.AppSessionsBean.ANONYMOUS_SESSION_KEY;
import static com.university.diploma.session.AppSessionsBean.SESSION_KEY_COOKIE;

@Controller
public class RecordController {

    @Autowired
    protected RecordDataService recordDataService;
    @Autowired
    protected UserDataService userDataService;
    @Autowired
    protected AppSessionsBean appSessionBean;

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/user-profile/{userId}/record")
    public ResponseEntity<RecordClientForm> recordAddSubmit(@RequestBody RecordForm form,
                                                            @PathVariable Long userId) {
        User user = userDataService.findById(userId);
        Long recordId = recordDataService.create(form, user);
        return ResponseEntity.ok(new RecordClientForm(recordId));
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/user-profile/{userId}/record/{recordId}")
    public ResponseEntity recordEditSubmit(@RequestBody RecordForm form,
                                           @PathVariable Long userId,
                                           @PathVariable Long recordId) {
        User user = userDataService.findById(userId);
        return recordDataService.update(recordId, form, user)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/user-profile/{userId}")
    public ResponseEntity<UserProfileForm> getUserProfile(@PathVariable Long userId,
                                                          @CookieValue(value = SESSION_KEY_COOKIE,
                                                                  defaultValue = ANONYMOUS_SESSION_KEY) String sessionKey) {
        AppSession appSession = getAppSession(sessionKey);
        if (appSession == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = appSession.getUser();
        UserProfileForm form = new UserProfileForm(user.getUsername(), user.getPassword(), user.getKeyword(),
                recordDataService.findByUser(user));
        return ResponseEntity.ok(form);
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/user-profile")
    public ResponseEntity<UserIdForm> getUserProfileId(@CookieValue(value = SESSION_KEY_COOKIE,
            defaultValue = ANONYMOUS_SESSION_KEY) String sessionKey) {
        AppSession appSession = getAppSession(sessionKey);
        if (appSession == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(new UserIdForm(appSession.getUser().getId()));
    }

    protected AppSession getAppSession(String sessionKey) {
        AppSession appSession = appSessionBean.getAppSessionBySessionKey(sessionKey);
        if (appSession == null || sessionKey.equals(ANONYMOUS_SESSION_KEY) || appSession.getUser() == null) {
            return null;
        }

        return appSession;
    }
}
