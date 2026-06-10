package ra.courseservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.courseservice.dto.CourseAvailableResponse;
import ra.courseservice.entity.Course;
import ra.courseservice.service.CourseService;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {



    private final CourseService courseService;
    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourse(@PathVariable Long courseId) {
        try {
            return ResponseEntity.ok(courseService.getById(courseId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/{courseId}/available")
    public ResponseEntity<?> getAvailabile(@PathVariable Long courseId) {
        try {
            Course course = courseService.getById(courseId);
            return ResponseEntity.ok(new CourseAvailableResponse(
                    course.getId(),
                    course.CheckAvailableSeat(),
                    course.getCapacity(),
                    course.getEnrolledCount()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }



    @PutMapping("/{courseId}/enroll")
    public ResponseEntity<?> enroll(@PathVariable Long courseId) {
        try {
            return ResponseEntity.ok(courseService.enroll(courseId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }





}
