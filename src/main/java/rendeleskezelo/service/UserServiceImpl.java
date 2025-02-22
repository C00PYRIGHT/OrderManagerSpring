package rendeleskezelo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rendeleskezelo.model.User;
import rendeleskezelo.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users != null ? users : new ArrayList<>();

    }

    @Override
    public void updateUser(User user) {
        User user1 = userRepository.findUserById(user.getId());
        user1.setId(user.getId());
        user1.setUsername(user.getUsername());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {

            user1.setPassword(passwordEncoder.encode(user.getPassword()));

        }
        user1.setRole(user.getRole());
        user1.setEmail(user.getEmail());
        userRepository.save(user1);


    }

    @Override
    public void deleteUser(Long id) {
        User user  = findUserById(id);
        userRepository.delete(user);
    }

    @Override
    public User findUserById(Long id) {
       return userRepository.findUserById(id);

    }
}
