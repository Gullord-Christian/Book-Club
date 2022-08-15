package com.christiangullord.bookclub.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.christiangullord.bookclub.models.BookModel;

@Repository
public interface BookRepo extends CrudRepository<BookModel, Long> {
	List<BookModel> findAll();
	List<BookModel> findByUserIdIs(Long userId);
	List<BookModel> findByBorrowerIdIs(Long userId);
	List<BookModel> findByBorrowerIdIsOrUserIdIs(Long borrowerID, Long userId);
}
