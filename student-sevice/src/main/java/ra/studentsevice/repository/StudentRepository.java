package ra.studentsevice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.studentsevice.entity.Student;

public interface StudentRepository extends JpaRepository<Student , Long > {



}
