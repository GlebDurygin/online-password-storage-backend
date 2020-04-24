package com.university.diploma.controller;

import com.university.diploma.entity.User;
import com.university.diploma.form.RecordClientForm;
import com.university.diploma.form.RecordForm;
import com.university.diploma.form.UserIdForm;
import com.university.diploma.form.UserProfileForm;
import com.university.diploma.service.CipherService;
import com.university.diploma.service.RecordDataService;
import com.university.diploma.service.UserDataService;
import com.university.diploma.session.AppSession;
import com.university.diploma.session.AppSessionsBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigInteger;

import static com.university.diploma.session.AppSessionsBean.ANONYMOUS_SESSION_ID;
import static com.university.diploma.session.AppSessionsBean.SESSION_ID_HEADER;

@Controller
public class RecordController {

    @Autowired
    protected RecordDataService recordDataService;
    @Autowired
    protected UserDataService userDataService;
    @Autowired
    protected AppSessionsBean appSessionBean;
    @Autowired
    protected CipherService cipherService;

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

    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = {SESSION_ID_HEADER, "Accept", "Content-Type"})
    @GetMapping("/user-profile/{userId}")
    public ResponseEntity<UserProfileForm> getUserProfile(@PathVariable Long userId,
                                                          @RequestHeader(value = SESSION_ID_HEADER,
                                                                  defaultValue = ANONYMOUS_SESSION_ID) String sessionId) {
        AppSession appSession = getAppSession(sessionId);
        if (appSession == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = appSession.getUser();
        byte[] username = cipherService.processBlockAES256(true, appSession.getSessionKey(), user.getUsername().getBytes());
        UserProfileForm form = new UserProfileForm(new BigInteger(username).toString(16),
                recordDataService.findByUser(user));
        return ResponseEntity.status(HttpStatus.OK)
                .body(form);
    }

    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = {SESSION_ID_HEADER, "Accept", "Content-Type"})
    @GetMapping("/user-profile")
    public ResponseEntity<UserIdForm> getUserProfileId(@RequestHeader(value = SESSION_ID_HEADER,
            defaultValue = ANONYMOUS_SESSION_ID) String sessionId) {
        AppSession appSession = getAppSession(sessionId);
        if (appSession == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        byte[] userId = cipherService.processBlockAES256(true, appSession.getSessionKey(), appSession.getUser().getId().toString().getBytes());
        UserIdForm form = new UserIdForm(new BigInteger(userId).toString(16));
        return ResponseEntity.status(HttpStatus.OK)
                .body(form);
    }

    protected AppSession getAppSession(String sessionId) {
        AppSession appSession = appSessionBean.getAppSessionBySessionId(sessionId);
        if (appSession == null || sessionId.equals(ANONYMOUS_SESSION_ID) || appSession.getUser() == null) {
            return null;
        }

        return appSession;
    }
}
