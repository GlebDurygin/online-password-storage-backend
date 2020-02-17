package com.university.diploma.controller;

import com.university.diploma.entity.Record;
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
public class RecordController {

    @Autowired
    protected RecordDataService recordDataService;
    @Autowired
    protected UserDataService userDataService;

    @GetMapping("/users/{userId}/record")
    public ModelAndView handleNewRecord(@PathVariable Long userId) {
        User user = userDataService.findById(userId);
        Record record = recordDataService.createWithoutSaving(user);
        return new ModelAndView("record", "record", record);
    }

    @GetMapping("/users/{userId}/record/{recordId}")
    public ModelAndView handleEditRecord(@PathVariable Long userId,
                                         @PathVariable Long recordId) {
        User user = userDataService.findById(userId);
        Record record = recordDataService.findByIdAndUser(user, recordId);
        return new ModelAndView("record", "record", record);
    }

    @PostMapping("/users/{userId}/record")
    public String recordAddSubmit(@RequestParam(name = "header") String header,
                                  @RequestParam(name = "data") String data,
                                  @RequestParam(name = "description") String description,
                                  @PathVariable Long userId) {
        User user = userDataService.findById(userId);
        recordDataService.create(header, data, description, user);
        return "redirect:/users/" + user.getId();
    }

    @PostMapping("/users/{userId}/record/{recordId}")
    public String recordEditSubmit(@RequestParam(name = "header") String header,
                                   @RequestParam(name = "data") String data,
                                   @RequestParam(name = "description") String description,
                                   @PathVariable Long userId,
                                   @PathVariable Long recordId) {
        User user = userDataService.findById(userId);
        recordDataService.update(recordId, header, data, description, user);
        return "redirect:/users/" + user.getId();
    }

    @GetMapping("/users/{userId}/delete/record/{recordId}")
    public ModelAndView handleDeleteRecord(@PathVariable Long userId,
                                           @PathVariable Long recordId,
                                           Model model) {
        recordDataService.remove(recordId);
        User user = userDataService.findById(userId);
        model.addAttribute("records", recordDataService.findByUser(user));
        return new ModelAndView("redirect:/users/" + userId);
    }
}
