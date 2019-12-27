package priv.howard.hystrix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.howard.hystrix.entity.Student;
import priv.howard.hystrix.interfaces.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/testHystrix")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public Collection<Student> findAll() {
        return studentService.findAll();
    }

    @GetMapping("/port")
    public String port() {
        return studentService.getPort();
    }
}
