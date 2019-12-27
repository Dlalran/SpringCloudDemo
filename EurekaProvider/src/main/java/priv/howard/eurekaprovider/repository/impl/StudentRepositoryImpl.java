package priv.howard.eurekaprovider.repository.impl;

import org.springframework.stereotype.Component;
import priv.howard.eurekaprovider.entity.Student;
import priv.howard.eurekaprovider.repository.StudentRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class StudentRepositoryImpl implements StudentRepository {
    /**
     * @Description Student实体类的Repository(即Dao)
     */

    private static Map<Integer, Student> studentMap;

    static {
        studentMap = new HashMap<>();
        studentMap.put(1, new Student(1, "howard", 21));
        studentMap.put(2, new Student(2, "john", 22));
        studentMap.put(3, new Student(3, "bill", 23));
    }

    @Override
    public Collection<Student> findAll() {
        return studentMap.values();
    }

    @Override
    public Student findById(int id) {
        return studentMap.get(id);
    }

    @Override
    public void saveOrUpdate(Student student) {
        studentMap.put(student.getId(), student);
    }

    @Override
    public void deleteById(int id) {
        studentMap.remove(id);
    }
}
