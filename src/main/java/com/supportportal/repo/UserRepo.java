package com.supportportal.repo;

import com.supportportal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long> {

    User findUserByUserName(String userName);
    User findUserByEmail(String email);
}
