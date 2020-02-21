package com.university.diploma.controller;

import com.university.diploma.dto.UserSignInClientDto;
import com.university.diploma.dto.UserSignInDBDto;
import com.university.diploma.dto.UserSignInServerDto;
import com.university.diploma.dto.UserSignUpDto;
import com.university.diploma.entity.User;
import com.university.diploma.service.RecordDataService;
import com.university.diploma.service.SRPService;
import com.university.diploma.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        return "redirect:/signin";
    }

    @GetMapping("/signin")
    public String handleSignIn(Model model) {
        return "signin";
    }

    @GetMapping("/signup")
    public String handleSignUp(Model model) {
        return "signup";
    }

    @PostMapping("/signup")
    public String signUpSubmit(@RequestParam(name = "username") String username,
                               @RequestParam(name = "password") String password,
                               @RequestParam(name = "keyword") String keyword) {
        UserSignUpDto userDto = srpService.signUp(username, password, keyword);
        if (userDataService.create(userDto)) {
            return "redirect:/signin";
        } else {
            return "signup";
        }
    }

    @PostMapping("/signin")
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
        return "signin";
    }

    @GetMapping("/users/{userId}")
    public ModelAndView handleUser(@PathVariable Long userId, Model model) {
        User user = userDataService.findById(userId);
        model.addAttribute("records", recordDataService.findByUser(user));
        return new ModelAndView("user", "user",
                user != null ? user : new User());
    }
}
