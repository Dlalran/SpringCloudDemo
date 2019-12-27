package priv.howard.eurekaconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import priv.howard.eurekaconsumer.entity.Student;

import java.util.Collection;

@RestController
@RequestMapping("/students")
public class ConsumerController {
    /**
     * @Description 消费者Controller,使用RestTemplate通过URL远程调用服务的REST接口 {@link StudentConroller}
     * 注意:不在Eureka Server注册也可以使用(即以REST风格对指定URL发出请求),但是注册后才能成为服务消费者并使用Spring Cloud
     */

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public Collection<Student> findAll() {

        final String url = "http://localhost:8081/students";
        /**
         * 通过RestTemplate的方法来以REST指定的请求方法来访问对应的url(第一个参数),并返回结果对象(第二个参数指定)
         * getForEntity返回的是整个Response的封装对象ResponseEntity,可以通过其getBody方法获得指定类型结果
         * getForObject则是返回封装后的指定类型结果对象
         */
//        Collection result = restTemplate.getForEntity("http://localhost:8081/students", Collection.class).getBody();
        Collection result = restTemplate.getForObject(url, Collection.class);
        return result;
    }

    @GetMapping("/{id}")
    public Student findById(@PathVariable("id") int id) {
//        地址传入参数时,注意将参数拼接进url
        final String url = "http://localhost:8081/students/" + id;
        Student result = restTemplate.getForObject(url, Student.class);
        return result;
    }

    @PostMapping
    public Student save(@RequestBody Student student) {
        final String url = "http://localhost:8081/students";
        /**
         * postForEntity第二个参数传递增加操作的数据对象,第三个指定返回的类型
         * 标准的RESTful设计中,新增后需要返回新增的完整对象,则第三个参数指定为对象类型本身(注意服务实现中要返回)
         */
        Student result = restTemplate.postForObject(url, student, Student.class);
        return result;
    }

    @PutMapping
    public void update(@RequestBody Student student) {
//         put()方法没有返回值
        final String url = "http://localhost:8081/students";
        restTemplate.put(url, student);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        final String url = "http://localhost:8081/students/" + id;
        restTemplate.delete(url);
    }
}
