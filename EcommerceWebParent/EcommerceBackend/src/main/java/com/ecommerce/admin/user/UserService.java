package com.ecommerce.admin.user;

import com.ecommerce.admin.paging.PagingAndSortingHelper;
import com.ecommerce.common.entity.Role;
import com.ecommerce.common.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired

    private PasswordEncoder passwordEncoder;

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    public void listByPage(int pageNumber, int pageSize, PagingAndSortingHelper helper) {
        helper.listEntities(pageNumber, pageSize, userRepository);
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    public User save(User user) {
        if (user.getId() != null && user.getPassword().isEmpty()) {
            User existingUser = userRepository.findById(user.getId()).orElseThrow();
            user.setPassword(existingUser.getPassword());
        } else {
            encodePassword(user);
        }

        return userRepository.save(user);
    }

    public User updateAccount(User userInForm) {
        User userInDB = userRepository.findById(userInForm.getId()).get();

        if (!userInForm.getPassword().isEmpty()) {
            userInDB.setPassword(userInForm.getPassword());
            encodePassword(userInDB);
        }

        if (userInForm.getPhotos() != null) {
            userInDB.setPhotos(userInForm.getPhotos());
        }

        userInDB.setFirstName(userInForm.getFirstName());
        userInDB.setLastName(userInForm.getLastName());

        return userRepository.save(userInDB);
    }

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepository.getByEmail(email);

        if (userByEmail == null) {
            return true;
        }

        return id != null && userByEmail.getId().equals(id);
    }

    public User getById(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException("Could not find user with id: " + id);
        }
    }

    public void deleteUser(Integer id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not find any user with Id: " + id);
        }
        userRepository.deleteById(id);
    }

    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepository.updateEnabledStatus(id, enabled);
    }

    public User getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

}
