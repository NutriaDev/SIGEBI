package sigebi.users.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sigebi.users.dtoRequest.UsersRequest;
import sigebi.users.dtoResponse.Response;
import sigebi.users.dtoResponse.UserResponse;
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

           return ApiResponse.internalError("Internal Error", e.getMessage());
        }
    }

    @GetMapping("api/get-all-users")
    public ResponseEntity<Response> getAllUsers(){
        try {
            List<UserResponse> users = usersService.getAllUsers();
            return ApiResponse.success("Users retrieved successfully", "All users fetched correctly", users);//

        } catch (Exception e) {
            log.error("Something went wrong while fetching all users: ", e);

            return ApiResponse.internalError("Internal Error", e.getMessage());
        }


    }

    @GetMapping("api/get-user-by-id")
    public ResponseEntity<Response> getUserById(
            @PathVariable Long id
    ){
        try {
            var user = usersService.getUserById(id);

            if(user == null) {
                return ApiResponse.notFound("User not found", "No user found wiyh Id: " + id);

            }

            return ApiResponse.success("User found", "User retrieved successfully", user);


        } catch (Exception e) {
            log.error("something went wrong when search this user: ", e);
            return ApiResponse.internalError("Internal Error", e.getMessage());
        }
    }

    @GetMapping("api/get-user-by-email")
    public ResponseEntity<Response> getUserByEmail(
            @PathVariable String email
    ){
        try {
            var userByEmail = usersService.getUserByEmail(email);

            if(userByEmail == null){
                return ApiResponse.notFound("User not found", "No user found with email: " + email);
            }

            return ApiResponse.success("User found", "User retrieved successfully by email", userByEmail);
        } catch (Exception e) {
            log.error("something went wrong when search this user by email: ", e);
            return ApiResponse.internalError("Internal Error", e.getMessage());
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
            return ApiResponse.internalError("Internal Error", e.getMessage());
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
            return ApiResponse.internalError("Internal Error", e.getMessage());
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
            return ApiResponse.internalError("Internal Error", e.getMessage());
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
            return ApiResponse.notFound("User not found", e.getMessage());
        } catch (Exception e) {
            return ApiResponse.internalError("Internal error", e.getMessage());
        }
    }




}
