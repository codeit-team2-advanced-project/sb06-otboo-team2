package codeit.sb06.otboo.user.controller;

import codeit.sb06.otboo.security.RequireRole;
import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import codeit.sb06.otboo.user.dto.request.UserLockUpdateRequest;
import codeit.sb06.otboo.user.dto.request.UserRoleUpdateRequest;
import codeit.sb06.otboo.user.dto.request.UserSliceRequest;
import codeit.sb06.otboo.user.dto.response.UserDtoCursorResponse;
import codeit.sb06.otboo.user.entity.Role;
import codeit.sb06.otboo.user.service.UserServiceImpl;
import jakarta.persistence.Column;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserCreateRequest userCreateRequest){
        log.info("Create user requested: {}", userCreateRequest);
        UserDto createdUser = userServiceImpl.create(userCreateRequest);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping
    @RequireRole(Role.ADMIN)
    public ResponseEntity<UserDtoCursorResponse> getUser(@ModelAttribute UserSliceRequest userSliceRequest){
        log.info("Get users requested: {}", userSliceRequest);
        UserDtoCursorResponse userDtoCursorResponse = userServiceImpl.getUsersCursor(userSliceRequest);
        return ResponseEntity.ok(userDtoCursorResponse);
    }

    @PatchMapping("/{userId}/role")
    @RequireRole(Role.ADMIN)
    public ResponseEntity<UserDto> updateRole(@PathVariable String userId, @RequestBody UserRoleUpdateRequest role){
        log.info("Update role requested: {}", role);
        UserDto updatedUser = userServiceImpl.changeUserRole(UUID.fromString(userId), role.role());
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{userId}/lock")
    @RequireRole(Role.ADMIN)
    public ResponseEntity<UserDto> updateLock(@PathVariable String userId, @RequestBody UserLockUpdateRequest lockUpdateRequest){
        log.info("Update lock requested: {}", lockUpdateRequest);
        UserDto updatedUser = userServiceImpl.changeLockStatus(UUID.fromString(userId), lockUpdateRequest.locked());
        return ResponseEntity.ok(updatedUser);
    }

}
