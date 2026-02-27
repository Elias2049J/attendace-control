package com.elias.attendancecontrol.controller;
import com.elias.attendancecontrol.config.SecurityUtils;
import com.elias.attendancecontrol.model.entity.SystemRole;
import com.elias.attendancecontrol.model.entity.User;
import com.elias.attendancecontrol.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String listUsers(Model model) {
        log.debug("Listing all users");
        model.addAttribute("users", userService.listUsers());
        model.addAttribute("activeMenu", "users");
        return "users/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ORG_OWNER', 'ORG_ADMIN', 'ADMIN')")
    public String showCreateForm(Model model) {
        log.debug("Showing user creation form");
        model.addAttribute("user", new User());
        model.addAttribute("activeMenu", "users");
        prepareFormModel(model);
        return "users/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ORG_OWNER', 'ORG_ADMIN', 'ADMIN')")
    public String createUser(@Valid @ModelAttribute User user,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            prepareFormModel(model);
            return "users/form";
        }
        try {
            if (user.getOrganizationRole() != null && user.getOrganizationRole().isOwner()) {
                model.addAttribute("error", "No tiene permisos para crear propietarios");
                model.addAttribute("user", user);
                prepareFormModel(model);
                return "users/form";
            }

            userService.createUser(user);
            redirectAttributes.addFlashAttribute("success", "Usuario creado exitosamente");
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            prepareFormModel(model);
            return "users/form";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ORG_OWNER', 'ORG_ADMIN', 'ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Showing edit form for user: {}", id);
        try {
            User targetUser = userService.getUserById(id);
            if (!securityUtils.canEditUser(targetUser)) {
                log.warn("User {} attempted to edit user {} without permission",
                        securityUtils.getCurrentUser().map(User::getUsername).orElse("unknown"), id);
                redirectAttributes.addFlashAttribute("error", "No tiene permisos para editar este usuario");
                return "redirect:/users";
            }
            model.addAttribute("user", targetUser);
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", "users");
            prepareFormModel(model);
            return "users/form";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users";
        }
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORG_OWNER', 'ORG_ADMIN', 'ADMIN')")
    public String updateUser(@PathVariable Long id,
                            @ModelAttribute("user") User userToUpdate,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        log.debug("Updating user: {}", id);
        try {
            User existingUser = userService.getUserById(id);

            if (!securityUtils.canEditUser(existingUser)) {
                redirectAttributes.addFlashAttribute("error", "No tiene permisos para editar este usuario");
                return "redirect:/users";
            }

            boolean hasError = false;
            if (userToUpdate.getName() == null || userToUpdate.getName().isBlank()) {
                result.rejectValue("name", "field.required", "El nombre es obligatorio");
                hasError = true;
            }
            if (userToUpdate.getLastname() == null || userToUpdate.getLastname().isBlank()) {
                result.rejectValue("lastname", "field.required", "El apellido es obligatorio");
                hasError = true;
            }
            if (userToUpdate.getEmail() == null || userToUpdate.getEmail().isBlank()) {
                result.rejectValue("email", "field.required", "El email es obligatorio");
                hasError = true;
            } else if (!userToUpdate.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                result.rejectValue("email", "field.invalid", "El email no tiene un formato válido");
                hasError = true;
            }

            if (hasError) {
                userToUpdate.setId(id);
                model.addAttribute("user", userToUpdate);
                model.addAttribute("isEdit", true);
                prepareFormModel(model);
                return "users/form";
            }

            userService.updateUser(id, userToUpdate);
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado exitosamente");
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            userToUpdate.setId(id);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", userToUpdate);
            model.addAttribute("isEdit", true);
            prepareFormModel(model);
            return "users/form";
        } catch (Exception e) {
            log.error("Unexpected error updating user {}: {}", id, e.getMessage(), e);
            userToUpdate.setId(id);
            model.addAttribute("error", "Error inesperado al actualizar el usuario: " + e.getMessage());
            model.addAttribute("user", userToUpdate);
            model.addAttribute("isEdit", true);
            prepareFormModel(model);
            return "users/form";
        }
    }

    @GetMapping("/search/results")
    public String searchUsersFragment(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false) SystemRole role,
            @RequestParam(required = false) Boolean active,
            Model model) {
        log.debug("Search fragment requested: query={}, role={}, active={}", query, role, active);
        List<User> users = userService.searchUsers(query, role, active);
        model.addAttribute("users", users);
        model.addAttribute("query", query);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedActive", active);
        return "fragments/user-search :: resultsTable";
    }
    @GetMapping("/search")
    public String searchUsers(@RequestParam String q, Model model) {
        log.debug("User search requested: query={}", q);
        List<User> users = userService.searchUsers(q, null, true);
        model.addAttribute("users", users);
        return "fragments/user-search :: userListItems";
    }
    private void prepareFormModel(Model model) {
        if (securityUtils.isSystemAdmin()) {
            model.addAttribute("systemRoles", SystemRole.values());
        } else {
            model.addAttribute("systemRoles", new SystemRole[]{SystemRole.USER});
        }
    }
}
