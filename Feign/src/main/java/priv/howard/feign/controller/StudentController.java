package priv.howard.feign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.howard.feign.entity.Student;
import priv.howard.feign.interfaces.StudentService;

import java.util.Collection;

@RestController
@RequestMapping("/feign")
public class StudentController {

    @Autowired
//    注意：添加Callback类后，要添加@Qualifier来注入指定Bean，value为服务提供者名
    @Qualifier("StudentProvider")
    private StudentService studentService;

    @GetMapping
    public Collection<Student> findAll() {
        Collection<Student> result = studentService.findAll();
        return result;
    }

    @GetMapping("/port")
    public String getPort() {
        return studentService.getPort();
    }

}
