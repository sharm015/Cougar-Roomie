package com.SE370.Cougar.Roomie.controller.view;

import com.SE370.Cougar.Roomie.controller.services.AssessmentService;
import com.SE370.Cougar.Roomie.model.CustomUserDetails;
import com.SE370.Cougar.Roomie.model.DTO.AssessmentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
public class AssessmentController {
    @Autowired
    AssessmentService assessmentService;

    @GetMapping("/user/assessment")
    public String presentAssessment(WebRequest webRequest, Model model) {
        AssessmentForm assessmentForm = new AssessmentForm();
        assessmentService.prepareAssessment(assessmentForm);
        model.addAttribute("assessmentForm", assessmentForm);
        return "assessment";
    }

    @PostMapping("/user/assessment")
    public String submitAssessment(@ModelAttribute AssessmentForm assessmentForm) {
        // Grab user id from logged in user object
        int userId = (((CustomUserDetails)SecurityContextHolder
                .getContext().getAuthentication().getPrincipal())
                .getUser_id());

        assessmentService.submitAssessment(assessmentForm, userId);

        return "assessment";
    }
}