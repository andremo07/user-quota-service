package com.vicarius.quota.repository;

import java.util.Optional;

public interface GenericRepository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    void deleteById(ID id);

    Class<T> getEntityClass();
}
