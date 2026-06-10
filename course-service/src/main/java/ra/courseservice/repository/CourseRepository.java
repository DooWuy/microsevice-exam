package ra.courseservice.repository;

import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import ra.courseservice.entity.Course;

public interface CourseRepository extends JpaRepository<Course , Long> {



}
