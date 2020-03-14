package com.university.diploma.controller;

import com.university.diploma.entity.Record;
import com.university.diploma.entity.User;
import com.university.diploma.form.RecordClientForm;
import com.university.diploma.form.RecordForm;
import com.university.diploma.form.UserProfileForm;
import com.university.diploma.service.RecordDataService;
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
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RecordController {

    @Autowired
    protected RecordDataService recordDataService;
    @Autowired
    protected UserDataService userDataService;

    @GetMapping("/user-profile/{userId}/record")
    public ModelAndView handleNewRecord(@PathVariable Long userId) {
        User user = userDataService.findById(userId);
        Record record = recordDataService.createWithoutSaving(user);
        return new ModelAndView("record", "record", record);
    }

    @GetMapping("/user-profile/{userId}/record/{recordId}")
    public ModelAndView handleEditRecord(@PathVariable Long userId,
                                         @PathVariable Long recordId) {
        User user = userDataService.findById(userId);
        Record record = recordDataService.findByIdAndUser(user, recordId);
        return new ModelAndView("record", "record", record);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/user-profile/{userId}/record")
    public ResponseEntity<RecordClientForm> recordAddSubmit(@RequestBody RecordForm form,
                                                            @PathVariable Long userId) {
        User user = userDataService.findById(userId);
        Long recordId = recordDataService.create(form, user);
        return ResponseEntity.ok(new RecordClientForm(recordId));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/user-profile/{userId}/record/{recordId}")
    public ResponseEntity recordEditSubmit(@RequestBody RecordForm form,
                                           @PathVariable Long userId,
                                           @PathVariable Long recordId) {
        User user = userDataService.findById(userId);
        return recordDataService.update(recordId, form, user)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @GetMapping("/user-profile/{userId}/delete/record/{recordId}")
    public ModelAndView handleDeleteRecord(@PathVariable Long userId,
                                           @PathVariable Long recordId,
                                           Model model) {
        recordDataService.remove(recordId);
        User user = userDataService.findById(userId);
        model.addAttribute("records", recordDataService.findByUser(user));
        return new ModelAndView("redirect:/users/" + userId);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/user-profile/{userId}")
    public ResponseEntity<UserProfileForm> getUserProfile(@PathVariable Long userId) {
        User user = userDataService.findById(userId);
        if (user != null) {
            UserProfileForm form = new UserProfileForm(user.getUsername(), user.getPassword(), user.getKeyword(),
                    recordDataService.findByUser(user));
            return ResponseEntity.ok(form);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
