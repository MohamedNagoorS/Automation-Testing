package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@SpringBootApplication
@Controller
public class SpringBootLoginApp {

    private static final int MAX_LENGTH = 255;
    private static final String VALID_USERNAME = "testUser";
    private static final String VALID_PASSWORD = "testPassword";
    private static final String ERROR_INVALID_CREDENTIALS = "Invalid credentials";
    private static final String ERROR_MISSING_PARAMETERS = "Missing required parameters";
    private static final String ERROR_METHOD_NOT_ALLOWED = "Method not allowed";
    private static final String ERROR_USERNAME_PASSWORD_NULL = "Username and password cannot be null";

    private static final String MODEL_ATTRIBUTE_ERROR = "error";
    private static final String VIEW_LOGIN = "login";
    private static final String VIEW_DASHBOARD = "dashboard";

    public static void main(String[] args) {
        SpringApplication.run(SpringBootLoginApp.class, args);
    }

    @PostMapping("/login")
    public String login(@RequestParam(required = true) String username,
                        @RequestParam(required = true) String password,
                        Model model) {
        
        // Validate input null check
        

        // Check for empty or whitespace-only inputs
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            model.addAttribute(MODEL_ATTRIBUTE_ERROR, ERROR_INVALID_CREDENTIALS);
            return VIEW_LOGIN;
        }

        // Validate input length
        if (username.length() > MAX_LENGTH || password.length() > MAX_LENGTH) {
            model.addAttribute(MODEL_ATTRIBUTE_ERROR, ERROR_INVALID_CREDENTIALS);
            return VIEW_LOGIN;
        }

        // Perform credential validation
        if (VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password)) {
            return VIEW_DASHBOARD;
        }

        model.addAttribute(MODEL_ATTRIBUTE_ERROR, ERROR_INVALID_CREDENTIALS);
        return VIEW_LOGIN;
    }
   


    // Handle missing parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMissingParams(MissingServletRequestParameterException ex, Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_ERROR, ERROR_MISSING_PARAMETERS);
        return VIEW_LOGIN;
    }

    // Add a request mapping for GET to properly handle method not allowed
    @RequestMapping(value = "/login", produces = "text/html")
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String methodNotAllowed(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_ERROR, ERROR_METHOD_NOT_ALLOWED);
        return VIEW_LOGIN;
    }@PostMapping("/loginDuplicate")
    public String loginDuplicate(@RequestParam(required = true) String username,
                                 @RequestParam(required = true) String password,
                                 Model model) {
    
        // Validate input null check
    
        // Check for empty or whitespace-only inputs
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            model.addAttribute(MODEL_ATTRIBUTE_ERROR, ERROR_INVALID_CREDENTIALS);
            return VIEW_LOGIN;
        }
    
       
    
        // Perform credential validation
        if (VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password)) {
            return VIEW_DASHBOARD;
        }
    
        model.addAttribute(MODEL_ATTRIBUTE_ERROR, ERROR_INVALID_CREDENTIALS);
        return VIEW_LOGIN;
    }
}
