package inventory.util;

import inventory.exception.UnauthorizedException;

import java.util.List;

public class RoleValidator {

    private static final List<String> ALLOWED_ROLES = List.of(
            "ADMIN",
            "INVENTORY_STAFF",
            "MAINTENANCE_TECH"
    );

    public static void validate(String role) {
        if (role == null || !ALLOWED_ROLES.contains(role.toUpperCase())) {
            throw new UnauthorizedException(
                    "No tiene permisos para realizar esta acción. Roles permitidos: "
                            + ALLOWED_ROLES);
        }
    }

    public static void requireAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException(
                    "Solo el administrador puede realizar esta acción");
        }
    }
}
