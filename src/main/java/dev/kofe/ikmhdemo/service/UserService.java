package dev.kofe.ikmhdemo.service;

import dev.kofe.ikmhdemo.model.User;
import dev.kofe.ikmhdemo.repo.RoleRepository;
import dev.kofe.ikmhdemo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    public User loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }
        return user;
    }

    public User findUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.orElse(new User());
    }

    public User findUserByUsernameIgnoreCase(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public void saveUserForUpdate(User user) {
        userRepository.save(user);
    }

    public boolean saveAdminUser(User user) {

        user.setRoles(Collections.singleton(roleRepository.getById(1L)));

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }


    public void reWriteUser (User user) {
        userRepository.save(user);
    }

    public boolean reWriteUserWithNewPassword(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }


    public boolean saveNewUser(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            return false;
        }
        user.setRoles(Collections.singleton(roleRepository.getById(2L)));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return true;
    }

    public User getUserByUsername(String username) {
        Optional<User> userFromDb = Optional.ofNullable(userRepository.findByUsername(username));
        return userFromDb.orElse(new User());
    }

    public boolean deleteUserById(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        } else {
            return false;
        }
    }

    /*******************************************
    * in this system, we use email as a username
    ********************************************/
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserUsername;
        if (principal instanceof UserDetails) {
            currentUserUsername = ((UserDetails)principal).getUsername();
        } else {
            currentUserUsername = principal.toString();
        }
        User currentUser = getUserByUsername(currentUserUsername);
        return currentUser;
    }

}
