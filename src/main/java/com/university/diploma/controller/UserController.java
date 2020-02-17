package com.university.diploma.controller;

import com.university.diploma.entity.User;
import com.university.diploma.service.RecordDataService;
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
        if (userDataService.create(username, password, keyword)) {
            return "redirect:/signin";
        } else {
            return "signup";
        }
    }

    @PostMapping("/signin")
    public String signIn(@RequestParam(name = "username") String username,
                         @RequestParam(name = "password") String password) {
        Long userId = userDataService.findUser(username, password);
        if (userId != null) {
            return "redirect:/users/" + userId;
        } else {
            return "signin";
        }
    }

    @GetMapping("/users/{userId}")
    public ModelAndView handleUser(@PathVariable Long userId, Model model) {
        User user = userDataService.findById(userId);
        model.addAttribute("records", recordDataService.findByUser(user));
        return new ModelAndView("user", "user",
                user != null ? user : new User());
    }
}
