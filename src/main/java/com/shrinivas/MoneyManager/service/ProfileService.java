package com.shrinivas.MoneyManager.service;

import com.shrinivas.MoneyManager.dto.AuthDto;
import com.shrinivas.MoneyManager.dto.ProfileDto;
import com.shrinivas.MoneyManager.entity.ProfileEntity;
import com.shrinivas.MoneyManager.repository.ProfileRepository;
import com.shrinivas.MoneyManager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    public final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDto registerProfile(ProfileDto profileDto){
       ProfileEntity newProfile= toEntity(profileDto);
       newProfile.setActivationToken(UUID.randomUUID().toString());

       newProfile=profileRepository.save(newProfile);
       String acttivationLink=activationURL+"/api/v1.0/activate?token="+newProfile.getActivationToken();
       String subject="Activate your Money Manager account";
       String body="Click on the following link to activate your account:"+acttivationLink;
       emailService.sendEmail(newProfile.getEmail(),subject,body);
       return   toDto(newProfile);

    }
    public ProfileEntity toEntity(ProfileDto profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }
    public ProfileDto toDto(ProfileEntity profileEntity) {
        return ProfileDto.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())

                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }
    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profile->{
            profile.setIsActive(true);
            profileRepository.save(profile);
            return true;
        })
                .orElse(false);
    }
     public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
     }
     public ProfileEntity getCurrentProfile(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(()->new UsernameNotFoundException("profile not found with email:"+authentication.getName()));
     }
     public ProfileDto getPublicProfile(String email){
        ProfileEntity currentUser=null;
        if(email==null){
            getCurrentProfile();
        }else {
            currentUser=profileRepository.findByEmail(email)
                    .orElseThrow(()->new UsernameNotFoundException("profile not found with email:"+email));

        }
        return ProfileDto.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())

                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
     }


    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto) {
        try{
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(),authDto.getPassword()));
//generate JWT token
            String token=jwtUtil.generateToken(authDto.getEmail());
            return Map.of(
                    "token",token,
                    "user",getPublicProfile(authDto.getEmail())
            );
        } catch (Exception e) {
            throw new RuntimeException("invalid email or password");
        }
    }
}
