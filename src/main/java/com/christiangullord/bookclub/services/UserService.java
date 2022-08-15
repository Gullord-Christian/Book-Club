package com.christiangullord.bookclub.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.christiangullord.bookclub.models.LoginUser;
import com.christiangullord.bookclub.models.UserModel;
import com.christiangullord.bookclub.repositories.UserRepo;

@Service
public class UserService {
	
	@Autowired
	private UserRepo userRepo;
	
	public UserModel register(UserModel newUser, BindingResult result) {
		// find user in database
		Optional<UserModel> optionalUser = userRepo.findByEmail(newUser.getEmail());
		// if the email is present we will reject(as email is already registered)
		if(optionalUser.isPresent()) {
			result.rejectValue("email", "unique", "Email is already registered");
		}
		// reject if password doesn't match confirm pass
    	if(!newUser.getPassword().equals(newUser.getConfirm())) { // if(!a.equals(b)) --> a!= b
    		result.rejectValue("confirm", "matches", "The confirm password does not match the password");
		}
		// return null if result has errors
		if(result.hasErrors()) {
			return null;
		}
		// hash and set password and save user to db
		String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt()); 
		newUser.setPassword(hashed);
		return userRepo.save(newUser);
	}

    public UserModel login(LoginUser newLoginObject, BindingResult result) {
        // find user by email in db
    	Optional<UserModel> potentialUser = userRepo.findByEmail(newLoginObject.getEmail());
    	// reject if not present
    	if(!potentialUser.isPresent()) {
    		result.rejectValue("email", "unique", "Email is not registered");
    		return null;
    	}
    	// grabbing the user to check validations
    	UserModel user = potentialUser.get();
    	// reject if bcrypt password match fails
    	if(!BCrypt.checkpw(newLoginObject.getPassword(), user.getPassword())) {
    		result.rejectValue("password", "matches", "Invalid password");
    	}
    	// return null if result has errors
    	if (result.hasErrors()) {
    		return null;
    	}
    	// return user object 
        return user;
    }	
    
    public UserModel oneUser(Long id) {
    	Optional<UserModel> optionalUser = userRepo.findById(id);
    	if(optionalUser.isPresent()) {
    		return optionalUser.get();
    	} else {
    		return null;
    	}
    }
}





