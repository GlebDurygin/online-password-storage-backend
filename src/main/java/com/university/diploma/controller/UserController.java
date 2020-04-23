package com.university.diploma.controller;

import com.university.diploma.dto.UserSignUpDto;
import com.university.diploma.entity.User;
import com.university.diploma.form.ServerAuthenticationForm;
import com.university.diploma.form.ServerCheckForm;
import com.university.diploma.form.SignUpForm;
import com.university.diploma.service.CipherService;
import com.university.diploma.service.SRPService;
import com.university.diploma.service.UserDataService;
import com.university.diploma.session.AppSession;
import com.university.diploma.session.AppSessionsBean;
import com.university.diploma.session.AuthenticationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;

import static com.university.diploma.session.AppSessionsBean.ANONYMOUS_SESSION_KEY;
import static com.university.diploma.session.AppSessionsBean.AUTHENTICATION_KEY_HEADER;

@Controller
public class UserController {

    @Autowired
    protected UserDataService userDataService;
    @Autowired
    protected SRPService srpService;
    @Autowired
    protected AppSessionsBean appSessionBean;
    @Autowired
    protected CipherService cipherService;

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(value = "/sign-up")
    public ResponseEntity<?> signUpSubmit(@RequestBody SignUpForm form) {
        UserSignUpDto userSignUpDto = cipherService.decryptSignUpForm(form, ANONYMOUS_SESSION_KEY);
        if (userDataService.create(userSignUpDto)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/sign-in-authentication")
    @ResponseBody
    public ResponseEntity<ServerAuthenticationForm> signInAuthentication(@RequestBody Map<String, byte[]> encryptedBody) {
        Map<String, String> body = cipherService.decryptBody(encryptedBody, ANONYMOUS_SESSION_KEY);
        User user = userDataService.findUserByUsername(body.get("username"));
        if (user == null || body.get("emphaticKeyA") == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        AppSession appSession = appSessionBean.createAppSession(user);
        AuthenticationDetails details = appSession.getAuthenticationDetails();
        details.setEmphaticKeyA(body.get("emphaticKeyA"));
        srpService.computeEmphaticKeyB(details);

        byte[] salt = cipherService.processBlockAES256(true, details.getAuthenticationKey(), details.getSalt().getBytes());
        byte[] emphaticKeyB = cipherService.processBlockAES256(true, details.getAuthenticationKey(), details.getEmphaticKeyB().getBytes());
        ServerAuthenticationForm form = new ServerAuthenticationForm(details.getAuthenticationKey(), new BigInteger(salt).toString(16),
                new BigInteger(emphaticKeyB).toString(16));

        return ResponseEntity.status(HttpStatus.OK)
                .body(form);
    }

    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = {AUTHENTICATION_KEY_HEADER, "Accept", "Content-Type"})
    @PostMapping("/sign-in-check")
    public ResponseEntity<ServerCheckForm> signInCheck(@RequestBody Map<String, byte[]> encryptedBody,
                                                       @RequestHeader(value = AUTHENTICATION_KEY_HEADER,
                                                               defaultValue = ANONYMOUS_SESSION_KEY) String authenticationKey) {
        AppSession appSession = appSessionBean.getAppSessionByAuthenticationKey(authenticationKey);
        Map<String, String> body = cipherService.decryptBody(encryptedBody, authenticationKey);
        if (appSession == null || appSession.getUser() == null || body.get("clientCheckValue") == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        AuthenticationDetails details = appSession.getAuthenticationDetails();
        String sessionKey = srpService.computeSessionKey(details);
        String clientCheckValue = srpService.computeClientCheckValue(details, appSession.getUser().getUsername(), sessionKey);

        if (!Objects.equals(body.get("clientCheckValue"), clientCheckValue)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String serverCheckValue = srpService.computeServerCheckValue(details, clientCheckValue, sessionKey);
        appSession.setAuthenticationDetails(null);
        appSession.setSessionKey(sessionKey);
        appSession.setSessionId(srpService.computeSessionId(clientCheckValue, serverCheckValue, sessionKey));
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ServerCheckForm(serverCheckValue));
    }
}
