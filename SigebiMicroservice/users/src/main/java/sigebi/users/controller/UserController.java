package sigebi.users.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sigebi.users.constants.ErrorTitles;
import sigebi.users.dto_request.RoleRequest;
import sigebi.users.dto_request.CreateUsersRequest;
import sigebi.users.dto_request.UpdateUserRequest;
import sigebi.users.dto_response.Response;
import sigebi.users.dto_response.RoleResponse;
import sigebi.users.dto_response.UserResponse;
import sigebi.users.service.RoleService;
import sigebi.users.service.UsersService;
import sigebi.users.util.ApiResponse;

import java.util.List;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private RoleService roleService;

    @PreAuthorize("hasAuthority('users.create')")
    @PostMapping("/api/users-create")
    public ResponseEntity<Response> userCreate(
            @Valid @RequestBody CreateUsersRequest createUsersRequest
    ){

        var user = usersService.createUser(createUsersRequest);

        return ApiResponse.success("User Created", "User registered correctly", user);

    }


    @PreAuthorize("hasAuthority('users.read')")
    @GetMapping("api/get-all-users")
    public ResponseEntity<Response> getAllUsers(){
        try {
            List<UserResponse> users = usersService.getAllUsers();
            return ApiResponse.success("Users retrieved successfully", "All users fetched correctly", users);//

        } catch (Exception e) {
            log.error("Something went wrong while fetching all users: ", e);

            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }


    }

    @PreAuthorize("hasAuthority('users.read')")
    @GetMapping("api/get-user-by-id/{id}")
    public ResponseEntity<Response> getUserById(
            @PathVariable Long id
    ){
        try {
            var user = usersService.getUserById(id);

            if(user == null) {
                return ApiResponse.notFound(ErrorTitles.USER_NOT_FOUND, "No user found wiyh Id: " + id);

            }

            return ApiResponse.success("User found", "User retrieved successfully", user);


        } catch (Exception e) {
            log.error("something went wrong when search this user: ", e);
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('users.read')")
    @GetMapping("api/get-user-by-email/{email}")
    public ResponseEntity<Response> getUserByEmail(
            @PathVariable String email
    ){
        try {
            var userByEmail = usersService.getUserByEmail(email);

            if(userByEmail.isEmpty()){
                return ApiResponse.notFound(ErrorTitles.USER_NOT_FOUND, "No user found with email: " + email);
            }

            return ApiResponse.success("User found", "User retrieved successfully by email", userByEmail);
        } catch (Exception e) {
            log.error("something went wrong when search this user by email: ", e);
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

    @GetMapping("api/get-all-by-active/{active}")
    public ResponseEntity<Response> getAllByStatus(
            @PathVariable Boolean active
    ){
        try {
         List<RoleResponse> roles = roleService.getRolesByStatus(active);
         return ApiResponse.success("Roles in true retrieved succesfully", "All roles fetched correctly", roles);

        } catch (Exception e) {
            log.error("Something went wrong while fetching all roles: ", e);

            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

    //roles

    @PreAuthorize("hasAuthority('roles.read')")
    @GetMapping("api/get-all-roles")
    public ResponseEntity<Response> getAllRoles(){
        try {
            List<RoleResponse> roles = roleService.getAllRoles();
            return ApiResponse.success("Users retrieved successfully", "All roles fetched correctly", roles);
        } catch (Exception e) {
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('roles.create')")
    @PostMapping("api/save-rol")
    public ResponseEntity<Response> saveRole(@RequestBody RoleRequest body) {
        try {
            var saved = roleService.saveRole(
                    body.getNameRole(),
                    body.getActive(),
                    body.getId()
                        );

            return ApiResponse.success("Role saved", "Role saved successfully", saved);

        } catch (Exception e) {
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('users.update')")
    @PatchMapping("api/edit-user/{id}")
    public ResponseEntity<Response> updateUser(
            @Valid @RequestBody UpdateUserRequest updateUserRequest,
            @PathVariable Long id
    ) {

        var updatedUser = usersService.updateUser(id, updateUserRequest);

        return ApiResponse.success(
                "User updated",
                "User updated correctly",
                updatedUser
        );
    }


    @PreAuthorize("hasAuthority('users.update')")
    @PatchMapping("api/deactive-user/{id}")
    public ResponseEntity<Response> deactiveUser(@PathVariable Long id) {

        var deactiveUser = usersService.toggleUserStatus(id, false);

        return ApiResponse.success(
                "User deactivated",
                "User updated with status false",
                deactiveUser
        );
    }

    @PreAuthorize("hasAuthority('users.update')")
    @PatchMapping("api/activate-user/{id}")
    public ResponseEntity<Response> activateUser(@PathVariable Long id) {

        var user = usersService.toggleUserStatus(id, true);

        return ApiResponse.success(
                "User activated",
                "User updated with status true",
                user
        );
    }





    @PreAuthorize("hasAuthority('users.delete')")
    @DeleteMapping("api/deletehard-user/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id) {

        var deletedUser = usersService.deleteUser(id);

        return ApiResponse.success(
                "User deleted",
                "User successfully removed from database",
                deletedUser
        );
    }

    @PreAuthorize("hasAuthority('roles.delete')")
    @DeleteMapping("api/deletehard-role/{id}")
    public ResponseEntity<Response> deleteRole(@PathVariable int id) {

        roleService.deleteRole(id);

        return ApiResponse.success(
                "Role deleted",
                "Role successfully removed from database",
                null
        );
    }




}
