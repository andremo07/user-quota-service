package com.vicarius.quota.repository;

import com.vicarius.quota.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    void deleteById(ID id);

    Class<T> getEntityClass();

    List<User> findByLastName(String lastName);
}
