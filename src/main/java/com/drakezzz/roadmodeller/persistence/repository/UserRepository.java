package com.drakezzz.roadmodeller.persistence.repository;

import com.drakezzz.roadmodeller.persistence.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String userName);

}
