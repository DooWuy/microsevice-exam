package ra.courseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.courseservice.entity.Course;
import ra.courseservice.repository.CourseRepository;
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Course getById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Khoong tim thay course co id : " + courseId));


    }

    public Course enroll(Long courseId) {
        Course course = getById(courseId);
        if (!course.CheckAvailableSeat()) {
            throw new IllegalStateException("khoa hoc da full ");
        }
        course.setEnrolledCount(course.getEnrolledCount() + 1);
        return course;
    }

}
