package com.university.diploma.controller;

import com.university.diploma.dto.UserSignInClientDto;
import com.university.diploma.dto.UserSignInDBDto;
import com.university.diploma.dto.UserSignInServerDto;
import com.university.diploma.dto.UserSignUpDto;
import com.university.diploma.entity.User;
import com.university.diploma.form.SignUpForm;
import com.university.diploma.service.RecordDataService;
import com.university.diploma.service.SRPService;
import com.university.diploma.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

/*    @CrossOrigin(origins = "*")*/
    @PostMapping(value = "/sign-up")
    public String signUpSubmit(@RequestBody SignUpForm form) {
        return "redirect:/sign-in";
/*        UserSignUpDto userDto = srpService.signUp(username, password, keyword);
        if (userDataService.create(userDto)) {
            return "redirect:/sign-in";
        } else {
            return "sign-up";
        }*/
    }

    @PostMapping("/sign-in")
    public String signIn(@RequestParam(name = "username") String username,
                         @RequestParam(name = "password") String password) {
        UserSignInClientDto clientDto = srpService.computeUsernameAndEmphaticKeyOnClient(username);
        UserSignInDBDto dbDto = userDataService.findUserByClientDto(clientDto);

        if (dbDto != null) {
            UserSignInServerDto serverDto = srpService.computeSaltAndEmphaticKeyOnServer(dbDto);

            String clientSessionKey = srpService.computeClientSessionKey(serverDto, username, password);
            String serverSessionKey = srpService.computeServerSessionKey(clientDto, dbDto);

            String clientCheckValue = srpService.computeClientCheckValue(username, dbDto.getSalt());
            String serverCheckValue = srpService.computeServerCheckValue(clientCheckValue);

            if (clientSessionKey.equals(serverSessionKey)) {
                Long userId = userDataService.findUser(username, password);
                return "redirect:/users/" + userId;
            }
        }
        return "sign-in";
    }

    @GetMapping("/users/{userId}")
    public ModelAndView handleUser(@PathVariable Long userId, Model model) {
        User user = userDataService.findById(userId);
        model.addAttribute("records", recordDataService.findByUser(user));
        return new ModelAndView("user", "user",
                user != null ? user : new User());
    }
}
