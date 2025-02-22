package rendeleskezelo.service;


import rendeleskezelo.model.User;
import rendeleskezelo.repository.UserRepository;

import java.util.List;

public interface UserService {
    void registerUser(User user);
    User findByUsername(String username);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(Long id);
    User findUserById(Long id);

}
