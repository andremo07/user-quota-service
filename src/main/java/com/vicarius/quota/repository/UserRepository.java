package com.vicarius.quota.repository;

import com.vicarius.quota.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository<T, K> {

    T save(T entity);

    Optional<T> findById(K id);

    void deleteById(K id);

    Class<T> getEntityClass();

    List<User> findByLastName(String lastName);
}
