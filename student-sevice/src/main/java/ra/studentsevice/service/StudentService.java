package ra.studentsevice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.studentsevice.client.CourseClient;
import ra.studentsevice.dto.CourseAvailableResponse;
import ra.studentsevice.entity.Student;
import ra.studentsevice.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseClient courseClient;


    public Student getById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("khong tim thay student with id " + studentId));
    }

    public void enroll(Long studentId, Long courseId) {
        Student student = getById(studentId);
        if (student.CheckEnrolled(courseId)) {
            throw new IllegalStateException("Student da dang ki ");
        }

        CourseAvailableResponse availability = courseClient.getAvailability(courseId);
        if (!availability.available()) {
            throw new IllegalStateException("Course da full ");
        }

        courseClient.enroll(courseId);
        student.getEnrolledCourses().add(courseId);
        studentRepository.save(student);
    }


}
