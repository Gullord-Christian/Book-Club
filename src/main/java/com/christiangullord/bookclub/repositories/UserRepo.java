package com.christiangullord.bookclub.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.christiangullord.bookclub.models.UserModel;

@Repository
public interface UserRepo extends CrudRepository <UserModel, Long>{
	Optional<UserModel> findByEmail(String email);

}