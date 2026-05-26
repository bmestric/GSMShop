package hr.bmestric.gsmshop.service;

import hr.bmestric.gsmshop.entity.AppUser;

import java.util.List;

public interface UserService {

    List<AppUser> findAll();

    void setActive(Long id, boolean active);
}
