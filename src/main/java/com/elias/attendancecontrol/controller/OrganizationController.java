package com.elias.attendancecontrol.controller;
import com.elias.attendancecontrol.config.SecurityUtils;
import com.elias.attendancecontrol.model.dto.OrganizationRegistrationDTO;
import com.elias.attendancecontrol.model.entity.Organization;
import com.elias.attendancecontrol.model.entity.OrganizationPlan;
import com.elias.attendancecontrol.model.entity.SystemRole;
import com.elias.attendancecontrol.model.entity.User;
import com.elias.attendancecontrol.service.LogService;
import com.elias.attendancecontrol.service.OrganizationService;
import com.elias.attendancecontrol.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;
    private final UserService userService;
    private final SecurityUtils securityUtils;
    private final LogService logService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public String listOrganizations(Model model) {
        log.debug("Listing all organizations for ADMIN");
        try {
            List<Organization> organizations = organizationService.findAll();
            model.addAttribute("organizations", organizations);
            model.addAttribute("activeMenu", "organizations-list");
            return "organizations/list";
        } catch (Exception e) {
            log.error("Error listing organizations: {}", e.getMessage());
            model.addAttribute("error", "Error al listar organizaciones: " + e.getMessage());
            model.addAttribute("organizations", List.of());
            model.addAttribute("activeMenu", "organizations-list");
            return "organizations/list";
        }
    }

    @GetMapping("/{slug}/members")
    @PreAuthorize("hasAnyRole('ORG_OWNER', 'ORG_ADMIN')")
    public String showOrganizationMembers(@PathVariable String slug, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Showing members of organization: {}", slug);
        try {
            User currentUser = securityUtils.getCurrentUserOrThrow();
            Organization org = currentUser.getOrganization();

            if (!org.getSlug().equals(slug)) {
                log.warn("User {} attempted to access members of organization {} without permission",
                        currentUser.getUsername(), slug);
                redirectAttributes.addFlashAttribute("error", "No tiene acceso a esta organización");
                return "redirect:/organizations/" + org.getSlug() + "/dashboard";
            }

            List<User> members = userService.findByOrganizationId(org.getId());
            model.addAttribute("organization", org);
            model.addAttribute("members", members);
            model.addAttribute("activeMenu", "organizations");
            return "organizations/members";
        } catch (Exception e) {
            log.error("Error showing organization members: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        log.debug("Showing organization registration form");
        model.addAttribute("registrationDTO", new OrganizationRegistrationDTO());
        model.addAttribute("plans", OrganizationPlan.values());
        return "organizations/register";
    }
    @PostMapping("/register")
    public String registerOrganization(@ModelAttribute OrganizationRegistrationDTO registrationDTO,
                                      HttpServletRequest request,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        Organization organization = registrationDTO.getOrganization();
        User user = registrationDTO.getUser();
        log.debug("Registering new organization: {} with owner: {}",
                  organization != null ? organization.getName() : "null",
                  user != null ? user.getUsername() : "null");
        if (organization == null || organization.getName() == null || organization.getName().isBlank() ||
            organization.getSlug() == null || organization.getSlug().isBlank() ||
            organization.getPlan() == null) {
            log.error("Organization data incomplete");
            model.addAttribute("error", "Todos los campos de la organización son obligatorios");
            model.addAttribute("plans", OrganizationPlan.values());
            model.addAttribute("registrationDTO", registrationDTO);
            return "organizations/register";
        }
        if (user == null || user.getUsername() == null || user.getUsername().isBlank() ||
            user.getEmail() == null || user.getEmail().isBlank() ||
            user.getName() == null || user.getName().isBlank() ||
            user.getLastname() == null || user.getLastname().isBlank() ||
            user.getPassword() == null || user.getPassword().isBlank()) {
            log.error("User data incomplete");
            model.addAttribute("error", "Todos los campos del administrador son obligatorios");
            model.addAttribute("plans", OrganizationPlan.values());
            model.addAttribute("registrationDTO", registrationDTO);
            return "organizations/register";
        }
        try {
            user.setSystemRole(SystemRole.USER);
            user.setActive(true);
            User savedUser = userService.registerUser(user);
            Organization savedOrg = organizationService.registerOrganization(organization, savedUser);
            logService.log(builder -> builder
                .eventType("ORGANIZATION_REGISTERED")
                .description("Nueva organización registrada: " + savedOrg.getName())
                .user(savedUser)
                .ipAddress(request.getRemoteAddr())
                .details("Plan: " + savedOrg.getPlan() + ", Slug: " + savedOrg.getSlug())
            );
            redirectAttributes.addFlashAttribute("success",
                "¡Organización registrada exitosamente! Ya puedes iniciar sesión.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            log.error("Error registering organization: {}", e.getMessage());
            logService.log(builder -> builder
                .eventType("ORGANIZATION_REGISTRATION_FAILED")
                .description("Error al registrar organización: " +
                        organization.getName())
                .ipAddress(request.getRemoteAddr())
                .details("Error: " + e.getMessage())
            );
            model.addAttribute("error", e.getMessage());
            model.addAttribute("plans", OrganizationPlan.values());
            model.addAttribute("registrationDTO", registrationDTO);
            return "organizations/register";
        }
    }
    @GetMapping("/{slug}/dashboard")
    @PreAuthorize("hasAnyRole('ORG_OWNER', 'ORG_ADMIN')")
    public String manageOrganization(@PathVariable String slug, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Showing organization dashboard for slug: {}", slug);
        try {
            User currentUser = securityUtils.getCurrentUserOrThrow();
            Organization org = currentUser.getOrganization();

            if (!org.getSlug().equals(slug)) {
                log.warn("User {} attempted to access organization {} without permission",
                        currentUser.getUsername(), slug);
                redirectAttributes.addFlashAttribute("error", "No tiene acceso a esta organización");
                return "redirect:/organizations/" + org.getSlug() + "/dashboard";
            }

            model.addAttribute("organization", org);
            model.addAttribute("userCount", organizationService.getUserCount(org.getId()));
            model.addAttribute("activityCount", organizationService.getActivityCount(org.getId()));
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("activeMenu", "organizations");
            return "organizations/dashboard";
        } catch (Exception e) {
            log.error("Error loading organization dashboard: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al cargar el panel de organización");
            return "redirect:/";
        }
    }

    @GetMapping("/{slug}/settings")
    @PreAuthorize("hasAnyRole('ORG_OWNER')")
    public String showSettingsForm(@PathVariable String slug, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Showing organization settings form for slug: {}", slug);
        try {
            User currentUser = securityUtils.getCurrentUserOrThrow();
            Organization org = currentUser.getOrganization();

            if (!org.getSlug().equals(slug)) {
                log.warn("User {} attempted to access settings of organization {} without permission",
                        currentUser.getUsername(), slug);
                redirectAttributes.addFlashAttribute("error", "No tiene acceso a esta organización");
                return "redirect:/organizations/" + org.getSlug() + "/dashboard";
            }

            model.addAttribute("organization", org);
            return "organizations/settings";
        } catch (Exception e) {
            log.error("Error showing settings form: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al cargar la configuración");
            return "redirect:/";
        }
    }

    @PostMapping("/{slug}/settings/update")
    @PreAuthorize("hasAnyRole('ORG_OWNER')")
    public String updateOrganization(@PathVariable String slug,
                                    @Valid @ModelAttribute Organization organization,
                                    BindingResult result,
                                    HttpServletRequest request,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        log.debug("Updating organization with slug: {}", slug);

        if (result.hasErrors()) {
            model.addAttribute("organization", organization);
            return "organizations/settings";
        }

        try {
            User currentUser = securityUtils.getCurrentUserOrThrow();
            Organization currentOrg = currentUser.getOrganization();

            if (!currentOrg.getSlug().equals(slug) || !currentOrg.getId().equals(organization.getId())) {
                log.warn("User {} attempted to update organization {} without permission",
                        currentUser.getUsername(), slug);
                logService.log(builder -> builder
                    .eventType("UNAUTHORIZED_ORG_UPDATE_ATTEMPT")
                    .description("Intento no autorizado de actualizar organización")
                    .user(currentUser)
                    .ipAddress(request.getRemoteAddr())
                    .details("Target org slug: " + slug)
                );
                redirectAttributes.addFlashAttribute("error", "No tiene permisos para editar esta organización");
                return "redirect:/organizations/" + currentOrg.getSlug() + "/dashboard";
            }

            organizationService.updateOrganization(organization.getId(), organization);

            logService.log(builder -> builder
                .eventType("ORGANIZATION_UPDATED")
                .description("Organización actualizada: " + organization.getName())
                .user(currentUser)
                .organization(currentOrg)
                .ipAddress(request.getRemoteAddr())
            );

            redirectAttributes.addFlashAttribute("success", "Organización actualizada exitosamente");
            return "redirect:/organizations/" + slug + "/dashboard";
        } catch (Exception e) {
            log.error("Error updating organization: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la organización: " + e.getMessage());
            return "redirect:/organizations/" + slug + "/dashboard";
        }
    }
    @GetMapping("/{slug}/admin/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAdminEditForm(@PathVariable String slug, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Showing admin edit form for organization: {}", slug);
        try {
            Organization org = organizationService.getOrganizationBySlug(slug);
            model.addAttribute("organization", org);
            model.addAttribute("activeMenu", "organizations-list");
            return "organizations/admin-edit";
        } catch (Exception e) {
            log.error("Error showing admin edit form: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al cargar la organización: " + e.getMessage());
            return "redirect:/organizations/all";
        }
    }

    @PostMapping("/{slug}/admin/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminUpdateOrganization(@PathVariable String slug,
                                         @Valid @ModelAttribute Organization organization,
                                         BindingResult result,
                                         HttpServletRequest request,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        log.debug("Admin updating organization: {}", slug);

        if (result.hasErrors()) {
            model.addAttribute("organization", organization);
            model.addAttribute("activeMenu", "organizations-list");
            return "organizations/admin-edit";
        }

        try {
            User currentUser = securityUtils.getCurrentUserOrThrow();
            Organization existingOrg = organizationService.getOrganizationBySlug(slug);

            existingOrg.setActive(organization.getActive());
            existingOrg.setPlan(organization.getPlan());

            organizationService.updateOrganization(existingOrg.getId(), existingOrg);

            logService.log(builder -> builder
                .eventType("ADMIN_ORGANIZATION_UPDATED")
                .description("System Admin actualizó organización: " + existingOrg.getName())
                .user(currentUser)
                .ipAddress(request.getRemoteAddr())
                .details("Plan: " + existingOrg.getPlan() + ", Active: " + existingOrg.getActive())
            );

            redirectAttributes.addFlashAttribute("success", "Organización actualizada exitosamente");
            return "redirect:/organizations/" + slug + "/admin/edit";
        } catch (Exception e) {
            log.error("Error admin updating organization: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la organización: " + e.getMessage());
            return "redirect:/organizations/all";
        }
    }

    @GetMapping("/members")
    public String listMembers() {
        log.debug("Redirecting to users list");
        return "redirect:/users";
    }
}
