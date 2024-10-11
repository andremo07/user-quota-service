package com.vicarius.quota.repository.mysql;

import com.vicarius.quota.model.User;
import com.vicarius.quota.repository.UserRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("mySqlUserRepository")
public interface DatabaseUserRepository extends CrudRepository<User, Long>, UserRepository<User, Long> {

    default Class<User> getEntityClass() {
        return User.class;
    }

    List<User> findByLastName(String lastName);
}
