package com.shrinivas.MoneyManager.controller;

import com.shrinivas.MoneyManager.dto.AuthDto;
import com.shrinivas.MoneyManager.dto.ProfileDto;
import com.shrinivas.MoneyManager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ProfileController {
    private final ProfileService profileService;
    @PostMapping("/register")
    public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDto){
        ProfileDto registerProfile=profileService.registerProfile(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }
    @GetMapping("/activate")
    public ResponseEntity<String>activateProfile(@RequestParam String token){
        boolean isActivated=profileService.activateProfile(token);
        if(isActivated){
            return ResponseEntity.ok("Profile activated successfully");

        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>>login(@RequestBody AuthDto authDto){
        try {
            if(profileService.isAccountActive(authDto.getEmail())){

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body( Map.of(
                        "message","Account is not active,please activate your account first"
                ));
            }
           Map<String,Object>response= profileService.authenticateAndGenerateToken(authDto);
            return ResponseEntity.ok(response);
        }catch (Exception e){
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            "message",e.getMessage()
    ));
        }
    }
    @GetMapping("/test")

    public String test(){
        return "test successful";
    }
}
