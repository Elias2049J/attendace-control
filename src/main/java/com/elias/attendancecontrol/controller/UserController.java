package com.elias.attendancecontrol.controller;

import com.elias.attendancecontrol.service.RoleService;
import com.elias.attendancecontrol.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String listUsers(Model model) {
        // TODO: Implementar listado de usuarios
        return "users/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // TODO: Implementar formulario de creación
        return "users/form";
    }

    @PostMapping
    public String createUser() {
        // TODO: Implementar creación de usuario
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        // TODO: Implementar formulario de edición
        return "users/form";
    }

    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id) {
        // TODO: Implementar actualización de usuario
        return "redirect:/users";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id) {
        // TODO: Implementar desactivación de usuario
        return "redirect:/users";
    }
}
