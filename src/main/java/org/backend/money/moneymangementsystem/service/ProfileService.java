package org.backend.money.moneymangementsystem.service;


import lombok.RequiredArgsConstructor;
import org.backend.money.moneymangementsystem.dto.AuthDTO;
import org.backend.money.moneymangementsystem.dto.ProfileDTO;
import org.backend.money.moneymangementsystem.entity.Profile;
import org.backend.money.moneymangementsystem.jwtUtils.JwtUtils;
import org.backend.money.moneymangementsystem.repository.ProfileRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils  jwtUtils;


    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        Profile newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        String activationLink =" http://localhost:8080/api/v1.0/activate?token= "+ newProfile.getActivationToken();
        String subject = " Active your money manger account ";
        String body = " click on the following link to activate your money manger account "+activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        return  toDTO(newProfile);
    }

    public Profile toEntity(ProfileDTO profileDTO) {
        return Profile.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .email(profileDTO.getEmail())
                .profileImg(profileDTO.getProfileImg())
                .createAt(profileDTO.getCreateAt())
                .updateAt(profileDTO.getUpdateAt())
                .build();
    }


    public ProfileDTO toDTO(Profile profile) {
        return ProfileDTO.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .profileImg(profile.getProfileImg())
                .createAt(profile.getCreateAt())
                .updateAt(profile.getUpdateAt())
                .build();
    }


    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                })
                .orElse(false);
    }

    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(Profile::getIsActive)
                .orElse(false);
    }

    public Profile getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
       return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("profile not found "+authentication.getName()));

    }

    public ProfileDTO getPublicProfile(String email){
        Profile currentUser = null;
        if(email==null){
            currentUser = getCurrentProfile();
        }else{
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(()-> new UsernameNotFoundException("profile not found "+email));
        }

        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImg(currentUser.getProfileImg())
                .createAt(currentUser.getCreateAt())
                .updateAt(currentUser.getUpdateAt())
                .build();

    }

    public Map<String,Object> authenticateAndGenerateToken(AuthDTO authDTO){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
            String token = jwtUtils.generateToken(authDTO.getEmail());
            return Map.of("token",token,
                          "user",getPublicProfile(authDTO.getEmail()));
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Invalid user and password");
        }
    }
}
