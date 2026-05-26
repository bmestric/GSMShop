package hr.bmestric.gsmshop.service.impl;

import hr.bmestric.gsmshop.entity.AppUser;
import hr.bmestric.gsmshop.repository.AppUserRepository;
import hr.bmestric.gsmshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;

    @Override
    public List<AppUser> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void setActive(Long id, boolean active) {
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(active);
            userRepository.save(user);
        });
    }
}
