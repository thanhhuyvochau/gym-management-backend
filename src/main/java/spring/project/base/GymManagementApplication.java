package spring.project.base;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import spring.project.base.constant.EAccountRole;
import spring.project.base.entity.Role;
import spring.project.base.repository.RoleRepository;

import java.util.List;


@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(title = "Apply Default Global SecurityScheme in springdoc-openapi", version = "1.0.0"),
        security = {@SecurityRequirement(name = "Bearer Authentication")})
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class GymManagementApplication {
    private final RoleRepository roleRepository;

    public GymManagementApplication(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(GymManagementApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            for (EAccountRole eAccountRole : EAccountRole.values()) {
                Role role = new Role();
                role.setCode(eAccountRole);
                role.setName(eAccountRole.getName());
                roles.add(role);
            }
            roleRepository.saveAll(roles);
        }
    }
}





