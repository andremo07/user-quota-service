package com.vicarius.quota.repository.factory;

import com.vicarius.quota.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class UserRepositoryFactory {

    private final static int STARTING_DAY_HOUR = 9;
    private final static int FINISHING_DAY_HOUR = 17;
    private final UserRepository mysqlUserRepository;
    private final UserRepository elasticSearchUserRepository;

    public UserRepositoryFactory(@Qualifier("mySqlUserRepository") UserRepository mysqlUserRepository,
                                 @Qualifier("elasticSearchUserRepository") UserRepository elasticsearchUserRepository) {
        this.mysqlUserRepository = mysqlUserRepository;
        this.elasticSearchUserRepository = elasticsearchUserRepository;
    }

    public UserRepository getUserRepository() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

        if (isDayTime(now)) {
            return mysqlUserRepository;
        }

        return elasticSearchUserRepository;
    }

    private boolean isDayTime(LocalDateTime now) {
        return now.getHour() >= STARTING_DAY_HOUR &&
                now.getHour() < FINISHING_DAY_HOUR;
    }
}
