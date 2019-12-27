package priv.howard.eurekaprovider.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import priv.howard.eurekaprovider.entity.Student;
import priv.howard.eurekaprovider.repository.StudentRepository;

import java.util.Collection;

@RestController
@RequestMapping("/students")
public class StudentController {
    /**
     * @Description Student的Controller,通过SpringMVC来实现RESTful接口
     */

//    通过Spring EL注入配置文件中指定的属性值,这里是获取当前服务端口
    @Value("${server.port}")
    private String port;

//    注入Repository
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public Collection<Student> findAll() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Student findById(@PathVariable("id") int id) {
        return studentRepository.findById(id);
    }

    @PostMapping
    public Student save(@RequestBody Student student) {
        studentRepository.saveOrUpdate(student);
        return student;
    }

    @PutMapping
    public void update(@RequestBody Student student) {
        studentRepository.saveOrUpdate(student);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") int id) {
        studentRepository.deleteById(id);
    }

//    获取当前服务端口，测试负载均衡等功能使用
    @GetMapping("/port")
    public String getPort() {
        return this.port;
    }
}

