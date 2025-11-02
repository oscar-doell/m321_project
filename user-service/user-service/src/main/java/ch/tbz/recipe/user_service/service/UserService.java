package ch.tbz.recipe.user_service.service;

import ch.tbz.recipe.user_service.dto.RegisterRequest;
import ch.tbz.recipe.user_service.model.User;
import ch.tbz.recipe.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.userRepository = repo;
        this.passwordEncoder = encoder;
    }

    @Transactional
    public User registerNewUser(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail().toLowerCase());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setRole("USER");
        return userRepository.save(u);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
}
