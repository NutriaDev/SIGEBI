package sigebi.users.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigebi.users.constants.ErrorTitles;
import sigebi.users.dto_request.RoleRequest;
import sigebi.users.dto_request.UsersRequest;
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

    @PostMapping("api/users-create")
    public ResponseEntity<Response> userCreate(
            @Valid @RequestBody UsersRequest usersRequest
    ){
        try{
            var user = usersService.createUser(usersRequest);

            return ApiResponse.success("User Created", "User registered correctly", user);

        }catch (Exception e){
            log.error("Something went wrong while creating user: ", e);

           return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }






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

    @GetMapping("api/get-user-by-email/{email}")
    public ResponseEntity<Response> getUserByEmail(
            @PathVariable String email
    ){
        try {
            var userByEmail = usersService.getUserByEmail(email);

            if(userByEmail == null){
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

    @GetMapping("api/get-all-roles")
    public ResponseEntity<Response> getAllRoles(){
        try {
            List<RoleResponse> roles = roleService.getAllRoles();
            return ApiResponse.success("Users retrieved successfully", "All roles fetched correctly", roles);
        } catch (Exception e) {
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

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






    @PatchMapping("api/edit-user/{id}")
    public ResponseEntity<Response> updateUser(
            @Valid @RequestBody UsersRequest usersRequest,
            @PathVariable Long id
    ){
        try {
            var updatedUser = usersService.updateUser(id, usersRequest);

            return ApiResponse.success("User updated", "user updated correctly", updatedUser);

        } catch (Exception e) {
            log.error("Something went wrong while updated user: ", e);
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }



    @PatchMapping("api/deactive-user/{id}")
    public ResponseEntity<Response> deactiveUser(
            @PathVariable Long id

    ){
        try {
            var deactiveUser = usersService.toggleUserStatus(id, false);
            return ApiResponse.success("User deactivated", "user updates with status false", deactiveUser);



        } catch (Exception e) {
            log.error("Something went wrong while deactivated user: ", e);
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }

    @PatchMapping("api/activate-user/{id}")
    public ResponseEntity<Response> activateUser(
            @PathVariable Long id
    ){
        try {
            var user = usersService.toggleUserStatus(id, true);
            return ApiResponse.success("User deactivated", "user updates with status false", user);

        } catch (Exception e) {
            log.error("Something went wrong while deactivated user: ", e);
            return ApiResponse.internalError(ErrorTitles.INTERNAL_ERROR, e.getMessage());
        }
    }





    @DeleteMapping("api/deletehard-user/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable Long id) {
        try {
            var deletedUser = usersService.deleteUser(id);
            return ApiResponse.success(
                    "User deleted",
                    "User successfully removed from database",
                    deletedUser
            );
        } catch (RuntimeException e) {
            return ApiResponse.notFound(ErrorTitles.USER_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.internalError("Internal error", e.getMessage());
        }
    }

    @DeleteMapping("api/deletehard-role/{id}")
    public ResponseEntity<Response> deleteRole(@PathVariable int id) {
        try {
            roleService.deleteRole(id);

            return ApiResponse.success(
                    "Role deleted",
                    "Role successfully removed from database",
                    null
            );

        } catch (RuntimeException e) {
            return ApiResponse.notFound(
                    "Role not found",
                    e.getMessage()
            );

        } catch (Exception e) {
            return ApiResponse.internalError(
                    "Internal error",
                    e.getMessage()
            );
        }
    }




}
