package com.christiangullord.bookclub.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.christiangullord.bookclub.models.BookModel;
import com.christiangullord.bookclub.models.UserModel;
import com.christiangullord.bookclub.repositories.BookRepo;

@Service
public class BookService {
	
	@Autowired
	private final BookRepo bookRepo;
	
	public BookService (BookRepo bookRepo) {
		this.bookRepo = bookRepo;
	}
	
	// find all
	public List<BookModel> allBooks(){
		return bookRepo.findAll();
	}
	// find one
	public BookModel oneBook(Long id) {
		Optional<BookModel> optionalBook = bookRepo.findById(id);
		if(optionalBook.isPresent()) {
			return optionalBook.get();
		} else {
			return null;
		}
	}
	
	// create
	public BookModel createBook(BookModel book) {
		return bookRepo.save(book);
	}
	// update
	public BookModel updateBook(BookModel book) {
		return bookRepo.save(book);
	}
	//delete
	public void deleteBook(Long id) {
		bookRepo.deleteById(id);
	}
	
	// remove borrower
	public void removeBorrower(BookModel book) {
		book.setBorrower(null);
		bookRepo.save(book);
	}
	
	// add borrower
	public void addBorrower(BookModel book, UserModel user) {
		book.setBorrower(user);
		bookRepo.save(book);
	}
	
	public List<BookModel> unborrowedBooks(UserModel user){
		return bookRepo.findByBorrowerIdIsOrUserIdIs(null, user.getId());
	}
	
	public List<BookModel> borrowedBooks(UserModel user){
		return bookRepo.findByBorrowerIdIs(user.getId());
	}
	
	public List<BookModel> myBooks(UserModel user){
		return bookRepo.findByUserIdIs(user.getId());
	}
}
