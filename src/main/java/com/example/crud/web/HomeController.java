/*
 * Copyright (c) K.X(Kevin Xin) 2017.
 * Find more details in http://xintq.net
 *
 */

package com.example.crud.web;

import com.example.crud.domain.LinkRepository;
import com.example.crud.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@Controller
public class HomeController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private LinkRepository linkRepository;

    @RequestMapping("/")
    public String toIndex(Locale locale, Model model) {
        model.addAttribute("customersCount", customerService.totalCount());
        model.addAttribute("linksCount", linkRepository.count());
        return "index";
    }

    @RequestMapping("/about")
    public String about(){
        return "about";
    }
}
