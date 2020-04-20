package com.SE370.Cougar.Roomie.controller.services;

import com.SE370.Cougar.Roomie.model.CustomUserDetails;
import com.SE370.Cougar.Roomie.model.DTO.Profile;
import com.SE370.Cougar.Roomie.model.DTO.UserInfo;
import com.SE370.Cougar.Roomie.model.entities.User;
import com.SE370.Cougar.Roomie.model.repositories.UserRepo;
import com.SE370.Cougar.Roomie.model.DTO.RegistrationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // Try to find user from db
        Optional<User> user = userRepository.findByUserName(userName);

        // Below works as follows, convert user into a CustomUserDetails object, if user is null throw error
        return user
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Error Not Found: " + userName));
    }

    /*
    Role: Grab user information from the DB for profile set up.
    Parameter: Profile DTO
     */
    public Profile prepareProfile(CustomUserDetails user){
        Profile profileInformation = new Profile();
        profileInformation.setFirst_name(user.getFirstName());
        profileInformation.setLast_name(user.getLastName());
        profileInformation.setGender(user.getGender());

        return profileInformation;
    }

    @Transactional
    public User createNewUser(RegistrationForm registrationForm) throws RuntimeException {

        if (!userRepository.existsByUserName(registrationForm.getUser_name())) { // check if username exists
            User user = new User();
            user.setEmail(registrationForm.getEmail());
            user.setUserName(registrationForm.getUser_name());
            user.setPassword(passwordEncoder.encode(registrationForm.getPassword()));
            user.setActive(true);
            return userRepository.save(user);
        } else {
            throw new RuntimeException("Sorry, that username is not available");
        }
    }

    @Transactional
    public List<UserInfo> getAllUsers () {
        CustomUserDetails thisUser = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findAll().stream()
                .filter(user -> !user.getUserName().equals(thisUser.getUsername()))
                .map(foundUser -> {
                    UserInfo converted = new UserInfo();
                    converted.setId(foundUser.getId());
                    converted.setUserName(foundUser.getUserName());
                    return converted;
                }).collect(Collectors.toList());
    }

    @Transactional
    public User updateFirstTimeUser(Profile profileInfoForm){
        User user = new User((((CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal())));
                
        user.registerProfileInfo(profileInfoForm);

        return userRepository.save(user);
    }

}