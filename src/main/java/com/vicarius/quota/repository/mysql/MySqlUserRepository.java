package com.vicarius.quota.repository.mysql;

import com.vicarius.quota.model.User;
import com.vicarius.quota.repository.GenericRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("mySqlUserRepository")
public interface MySqlUserRepository extends CrudRepository<User, Long>, GenericRepository<User, Long> {

    default Class<User> getEntityClass() {
        return User.class;
    }
}
