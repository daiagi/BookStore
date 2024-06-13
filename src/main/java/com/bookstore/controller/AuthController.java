package com.bookstore.controller;

import com.bookstore.dto.UserDTO;
import com.bookstore.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/book-store/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    private static final String ERROR = "error";

    @PostMapping(value = "/register", consumes = "application/x-www-form-urlencoded")
    public ModelAndView register(@ModelAttribute UserDTO userDTO,
            @RequestParam(required = false) String redirectTo, HttpServletResponse response) {
        try {
            userService.registerUser(userDTO);
            String token = userService.login(userDTO);
            return handleSuccessfulAuth(response, token, redirectTo);
        } catch (IllegalArgumentException e) {
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject(ERROR, e.getMessage());
            return modelAndView;
        }
    }

    @PostMapping(value = "/login", consumes = "application/x-www-form-urlencoded")
    public ModelAndView login(@ModelAttribute UserDTO userDTO,
            @RequestParam(required = false) String redirectTo,
            HttpServletResponse response) {
        try {
            String token = userService.login(userDTO);
            return handleSuccessfulAuth(response, token, redirectTo);
        } catch (IllegalArgumentException e) {
            ModelAndView modelAndView = new ModelAndView("login");
            modelAndView.addObject(ERROR, e.getMessage());
            return modelAndView;
        }
    }

    private ModelAndView handleSuccessfulAuth(HttpServletResponse response, String token, String redirectTo) {
        Cookie cookie = new Cookie("JWT_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        if (redirectTo != null) {
            return new ModelAndView("redirect:" + redirectTo);
        }
        return new ModelAndView("redirect:/book-store/books");
    }

    @GetMapping("/register")
    public ModelAndView getRegisterForm(@RequestParam(required = false) String error) {
        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject(ERROR, error);
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView getLoginForm(@RequestParam(required = false) String error) {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject(ERROR, error);
        return modelAndView;
    }
}
