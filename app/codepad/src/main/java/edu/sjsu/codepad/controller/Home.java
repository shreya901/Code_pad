/**
 * SJSU CS 218 Fall 2019 TEAM-5
 */

package edu.sjsu.codepad.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Home {

    @RequestMapping("/")
    public String home() {
        return "index";
    }
}
