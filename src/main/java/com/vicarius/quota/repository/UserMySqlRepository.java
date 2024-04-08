package com.vicarius.quota.repository;

import com.vicarius.quota.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("mySqlUserRepository")
public interface UserMySqlRepository extends CrudRepository<User, Long>, GenericRepository<User, Long> {

    default Class<User> getEntityClass() {
        return User.class;
    }
}
