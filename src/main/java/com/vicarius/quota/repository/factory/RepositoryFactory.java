package com.vicarius.quota.repository.factory;

import com.vicarius.quota.repository.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Component
public class RepositoryFactory<T, ID> {

    private static final int STARTING_DAY_HOUR = 9;
    private static final int FINISHING_DAY_HOUR = 17;
    private final Map<Class<?>, UserRepository<T, ID>> databaseRepositories;
    private final Map<Class<?>, UserRepository<T, ID>> elasticSearchRepositories;

    public RepositoryFactory(ApplicationContext applicationContext) {
        this.databaseRepositories = new HashMap<>();
        this.elasticSearchRepositories = new HashMap<>();

        Map<String, UserRepository> allRepositories = applicationContext.getBeansOfType(UserRepository.class);
        for (Map.Entry<String, UserRepository> entry : allRepositories.entrySet()) {
            UserRepository repository = entry.getValue();

            if (repository instanceof CrudRepository) {
                databaseRepositories.put(repository.getEntityClass(), repository);
            } else {
                elasticSearchRepositories.put(repository.getEntityClass(), repository);
            }
        }
    }

    public UserRepository<T, ID> getRepository(Class<T> entityClass) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

        if (isDayTime(now)) {
            return databaseRepositories.get(entityClass);
        }

        return elasticSearchRepositories.get(entityClass);
    }

    private boolean isDayTime(LocalDateTime now) {
        return now.getHour() >= STARTING_DAY_HOUR && now.getHour() < FINISHING_DAY_HOUR;
    }
}
