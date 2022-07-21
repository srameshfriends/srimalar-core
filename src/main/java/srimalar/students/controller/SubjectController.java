package srimalar.students.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Controller
public class SubjectController {

    @RequestMapping("/subjects")
    @ResponseBody
    public ResponseEntity<List<String>> subjects() {
        return new ResponseEntity<>(Arrays.asList("Tamil, Hindi, English"), HttpStatus.OK);
    }
}
