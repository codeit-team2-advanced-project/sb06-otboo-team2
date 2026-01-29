package codeit.sb06.otboo.user.controller;

import codeit.sb06.otboo.user.dto.UserDto;
import codeit.sb06.otboo.user.dto.request.UserCreateRequest;
import codeit.sb06.otboo.user.dto.request.UserSliceRequest;
import codeit.sb06.otboo.user.dto.response.UserDtoCursorResponse;
import codeit.sb06.otboo.user.service.UserServiceImpl;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public ResponseEntity<UserDtoCursorResponse> getUser(@ModelAttribute UserSliceRequest userSliceRequest){
        log.info("Get users requested: {}", userSliceRequest);
        UserDtoCursorResponse userDtoCursorResponse = userServiceImpl.getUsersCursor(userSliceRequest);
        return ResponseEntity.ok(userDtoCursorResponse);
    }
}
