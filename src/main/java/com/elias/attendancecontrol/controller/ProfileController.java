package com.elias.attendancecontrol.controller;
import com.elias.attendancecontrol.config.SecurityUtils;
import com.elias.attendancecontrol.model.entity.User;
import com.elias.attendancecontrol.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Slf4j
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;
    private final SecurityUtils securityUtils;
    @GetMapping
    public String viewProfile(Model model) {
        log.debug("Viewing profile");
        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));
        model.addAttribute("user", currentUser);
        model.addAttribute("activeMenu", "profile");
        return "profile/view";
    }
    @GetMapping("/edit")
    public String editProfile(Model model) {
        log.debug("Showing edit profile form");
        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));
        model.addAttribute("user", currentUser);
        model.addAttribute("activeMenu", "profile");
        return "profile/edit";
    }
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("user") User user,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        log.debug("Updating profile");
        User currentUser = securityUtils.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));

        boolean hasError = false;
        if (user.getName() == null || user.getName().isBlank()) {
            result.rejectValue("name", "field.required", "El nombre es obligatorio");
            hasError = true;
        }
        if (user.getLastname() == null || user.getLastname().isBlank()) {
            result.rejectValue("lastname", "field.required", "El apellido es obligatorio");
            hasError = true;
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            result.rejectValue("email", "field.required", "El email es obligatorio");
            hasError = true;
        } else if (!user.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            result.rejectValue("email", "field.invalid", "El email no tiene un formato válido");
            hasError = true;
        }

        if (hasError) {
            model.addAttribute("user", currentUser);
            model.addAttribute("activeMenu", "profile");
            return "profile/edit";
        }

        try {
            user.setId(currentUser.getId());
            user.setUsername(currentUser.getUsername());
            user.setSystemRole(currentUser.getSystemRole());
            user.setOrganization(currentUser.getOrganization());
            user.setOrganizationRole(currentUser.getOrganizationRole());
            userService.updateUser(currentUser.getId(), user);
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
            return "redirect:/profile";
        } catch (Exception e) {
            log.error("Error updating profile", e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", currentUser);
            model.addAttribute("activeMenu", "profile");
            return "profile/edit";
        }
    }
}
