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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;

import static com.university.diploma.session.AppSessionsBean.ANONYMOUS_SESSION_ID;
import static com.university.diploma.session.AppSessionsBean.AUTHENTICATION_SESSION_ID_COOKIE;

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
        UserSignUpDto userSignUpDto = cipherService.decryptSignUpFormRSA(form);
        if (userDataService.create(userSignUpDto)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/sign-in-authentication")
    @ResponseBody
    public ResponseEntity<ServerAuthenticationForm> signInAuthentication(@RequestBody Map<String, byte[]> encryptedBody) {
        Map<String, String> body = cipherService.decryptBodyRSA(encryptedBody);
        User user = userDataService.findUserByUsername(body.get("username"));
        if (user == null || body.get("emphaticKeyA") == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        AppSession appSession = appSessionBean.createAppSession(user);
        AuthenticationDetails details = appSession.getAuthenticationDetails();
        details.setEmphaticKeyA(body.get("emphaticKeyA"));
        srpService.computeEmphaticKeyB(details);

        byte[] salt = cipherService.processBlockRSA(true, details.getSalt().getBytes());
        byte[] emphaticKeyB = cipherService.processBlockRSA(true, details.getEmphaticKeyB().getBytes());
        ServerAuthenticationForm form = new ServerAuthenticationForm(new BigInteger(salt).toString(16),
                new BigInteger(emphaticKeyB).toString(16));

        return ResponseEntity.status(HttpStatus.OK)
                .header("Set-Cookie", AUTHENTICATION_SESSION_ID_COOKIE + "=" + details.getAuthenticationKey())
                .body(form);
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/sign-in-check")
    public ResponseEntity<ServerCheckForm> signInCheck(@RequestBody Map<String, byte[]> encryptedBody,
                                                       @CookieValue(value = AUTHENTICATION_SESSION_ID_COOKIE,
                                                               defaultValue = ANONYMOUS_SESSION_ID) String authenticationKey) {
        AppSession appSession = appSessionBean.getAppSessionByAuthenticationKey(authenticationKey);
        if (appSession == null || appSession.getUser() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Map<String, String> body = cipherService.decryptBodyRSA(encryptedBody);
        if (body.get("clientCheckValue") == null) {
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
        byte[] serverCheckValueBytes = cipherService.processBlockRSA(true, serverCheckValue.getBytes());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ServerCheckForm(new BigInteger(serverCheckValueBytes).toString(16)));
    }
}
