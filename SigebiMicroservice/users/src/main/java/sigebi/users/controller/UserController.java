package sigebi.users.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sigebi.users.dtoRequest.UsersRequest;
import sigebi.users.dtoResponse.Response;
import sigebi.users.service.RoleService;
import sigebi.users.service.UsersService;

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

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Response.builder()
                            .status("success")
                            .title("User created")
                            .message("User registered correctly")
                            .body(user)
                            .build()
                    );

        }catch (Exception e){
            log.error("Something went wrong while creating user: ", e);

            return ResponseEntity.internalServerError()
                    .body(Response.builder()
                            .status("false")
                            .title("Internal Error")
                            .message(e.getMessage())
                            .build()
                    );
        }
    }


}
