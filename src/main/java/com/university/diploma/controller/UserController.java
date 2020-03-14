package com.university.diploma.controller;

import com.university.diploma.dto.UserSignInClientDto;
import com.university.diploma.dto.UserSignInDBDto;
import com.university.diploma.dto.UserSignInServerDto;
import com.university.diploma.dto.UserSignUpDto;
import com.university.diploma.entity.User;
import com.university.diploma.form.SignInClientForm;
import com.university.diploma.form.SignInForm;
import com.university.diploma.form.SignUpForm;
import com.university.diploma.form.UserProfileForm;
import com.university.diploma.service.RecordDataService;
import com.university.diploma.service.SRPService;
import com.university.diploma.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;

@Controller
public class UserController {

    @Autowired
    protected UserDataService userDataService;
    @Autowired
    protected RecordDataService recordDataService;
    @Autowired
    protected SRPService srpService;

    @GetMapping("/")
    public String redirectToSignIn(Model model) {
        return "redirect:/sign-in";
    }

    @GetMapping("/sign-in")
    public String handleSignIn(Model model) {
        return "sign-in";
    }

    @GetMapping("/sign-up")
    public String handleSignUp(Model model) {
        return "sign-up";
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(value = "/sign-up")
    public ResponseEntity signUpSubmit(@RequestBody SignUpForm form) {
        UserSignUpDto userDto = srpService.signUp(form);
        if (userDataService.create(userDto)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/sign-in")
    public ResponseEntity<SignInClientForm> signIn(@RequestBody SignInForm form) {
        UserSignInClientDto clientDto = srpService.computeUsernameAndEmphaticKeyOnClient(form.getUsername());
        UserSignInDBDto dbDto = userDataService.findUserByClientDto(clientDto);

        if (dbDto != null) {
            UserSignInServerDto serverDto = srpService.computeSaltAndEmphaticKeyOnServer(dbDto);

            String clientSessionKey = srpService.computeClientSessionKey(serverDto, form);
            String serverSessionKey = srpService.computeServerSessionKey(clientDto, dbDto);

            String clientCheckValue = srpService.computeClientCheckValue(form.getUsername(), dbDto.getSalt());
            String serverCheckValue = srpService.computeServerCheckValue(clientCheckValue);

            if (clientSessionKey.equals(serverSessionKey)) {
                Long userId = userDataService.findUser(form);
                return ResponseEntity.ok(new SignInClientForm(clientSessionKey, userId));
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
