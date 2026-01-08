package com.glideclouds.springbootmongocrud.repository;

import com.glideclouds.springbootmongocrud.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
