package com.christiangullord.bookclub.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.christiangullord.bookclub.models.BookModel;
import com.christiangullord.bookclub.models.LoginUser;
import com.christiangullord.bookclub.models.UserModel;
import com.christiangullord.bookclub.services.BookService;
import com.christiangullord.bookclub.services.UserService;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private BookService bookService;
	
	/// render reg/log-in form
	@GetMapping("/")
	public String index(Model model) {
		// create an empty user and loginUser for the jsp form
		model.addAttribute("newUser", new UserModel()); 
		model.addAttribute("newLogin", new LoginUser());
		return "index.jsp";
	}
	
	// process register logic
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("newUser") UserModel newUser, 
			BindingResult result, Model model, HttpSession session) {
		// this will call the register method and create a new user
			UserModel user = userService.register(newUser, result);
		if(result.hasErrors()) {
			// we need to make sure the page is able to re-render with the new login
			// register model (newUser) is already available
			model.addAttribute("newLogin", new LoginUser());
			return "index.jsp";
		}
		
		// store id from the db in the session
		session.setAttribute("userId", user.getId());
		return "redirect:/books";
	}
	
	// process login logic
	@PostMapping("/login")
	public String login(@Valid @ModelAttribute("newLogin") LoginUser newLogin, 
			BindingResult result, Model model, HttpSession session) {
		UserModel user = userService.login(newLogin, result);
		if(result.hasErrors() || user == null) {
			// making sure loginObject is already in place
			model.addAttribute("newUser", new UserModel());
			return "index.jsp";
		}
		// storing id from the database in the session
		session.setAttribute("userId", user.getId());
		session.setAttribute("firstName", user.getFirstName());
		session.setAttribute("lastName", user.getLastName());
		
		return "redirect:/books";
	}
	
	// render dashboard
	@GetMapping("/books")
	public String dashboard( HttpSession session, Model model) {
		Long userId = (Long) session.getAttribute("userId");
		if(userId == null) {
			return "redirect:/logout";
		} 
		if (session.getAttribute("userId") == null)
			return "redirect:/logout";
		
		List<BookModel> bookList = bookService.allBooks();
		model.addAttribute("user", userService.oneUser(userId));
		model.addAttribute("bookList", bookList);
		return "dashboard.jsp";

	}
	
	// render book form
	@GetMapping("/books/new")
	public String showBookForm(@ModelAttribute("newBook") BookModel book, HttpSession session) {
		if(session.getAttribute("userId") == null ) {
				return "redirect:/logout";
		}
		return "bookForm.jsp";
	}
	
	// process book form
	@PostMapping("/books/new")
	public String createBook(@Valid @ModelAttribute("newBook") BookModel book, 
			BindingResult result, HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if(userId == null) {
			return "redirect:/logout";
		}
		if(result.hasErrors()) {
			return "bookForm.jsp";
		} else {
			bookService.createBook(new BookModel(book.getTitle(), book.getAuthor(), 
					book.getMyThoughts(), userService.oneUser(userId)));
			return "redirect:/books";
		} 
	}
	
//	 find one book
	@GetMapping("/books/{id}")
	public String showDetails(@PathVariable("id") Long id, Model model) {
		model.addAttribute("book", bookService.oneBook(id));
		model.addAttribute("user", userService.oneUser(id));
		return "details.jsp";
	}
	
	/// edit 
	@GetMapping("/books/edit/{id}")
	public String showEdit(@PathVariable("id") Long id, Model model, HttpSession session) {
		if(session.getAttribute("userId") == null) {
			return "redirect:/logout";
		}
		model.addAttribute("book", bookService.oneBook(id));
		return "editForm.jsp";
		}
	
	
	
	// process form
	@PutMapping("/books/edit/{id}")
	public String editForm(@PathVariable("id") Long id, @Valid @ModelAttribute("book") BookModel book, 
			BindingResult result, HttpSession session) {
		if(session.getAttribute("userId") == null) {
			return "redirect:/logout";
		}
		if(result.hasErrors()) {
			return "editForm.jsp";
		} else {
			bookService.updateBook(book);
			return "redirect:/books";
		}
	}
	
	
	/// delete //
	@DeleteMapping("/books/delete/{id}")
	public String destroyBook(@PathVariable("id") Long id, HttpSession session) {
		if(session.getAttribute("userId") == null) {
			return "redirect:/logout";
		}
		bookService.deleteBook(id);
		return "redirect:/bookmarket";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	/// books borrowed logic //
	
	@GetMapping("/bookmarket")
	public String bookMarket(HttpSession session, Model model) {
		Long userId = (Long) session.getAttribute("userId");
		if(userId == null) {
			return "redirect:/logout";
		}
		model.addAttribute("user", userService.oneUser(userId));
		
		List<BookModel> bookList = bookService.unborrowedBooks(userService.oneUser(userId));
		model.addAttribute("books", bookList);
		
		List<BookModel> myBooks = bookService.borrowedBooks(userService.oneUser(userId));
		model.addAttribute("myBooks", myBooks);
		
		return "bookMarket.jsp";
	}
	
	@RequestMapping("/bookmarket/{bookID}")
	public String borrowBook(@PathVariable("bookID") Long bookID, HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if(userId == null) {
			return "redirect:/logout";
		}
		bookService.addBorrower(bookService.oneBook(bookID), userService.oneUser(userId));
		return "redirect:/bookmarket";
	}
	
	
	@RequestMapping("/bookmarket/return/{bookID}")
	public String returnBook(@PathVariable("bookID") Long bookID, HttpSession session) {
		if(session.getAttribute("userId") == null) {
			return "redirect:/logout";
		}
		bookService.removeBorrower(bookService.oneBook(bookID));
		
		return "redirect:/bookmarket";
	}
}













