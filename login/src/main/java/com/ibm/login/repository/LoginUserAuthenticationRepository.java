package com.ibm.login.repository;

import com.ibm.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginUserAuthenticationRepository extends JpaRepository<User, String> {

    /*
     * Apart from the standard CRUD methods already available in JPA Repository,
     * based on our requirements, we might need to create few query methods for
     * getting specific data from the database.
     */

    /*
     * This method will validate a user from database by username and password.
     */

    User findByUserIdAndUserPassword(String userId, String userPassword);
}
