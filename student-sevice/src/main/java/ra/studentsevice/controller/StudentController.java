package ra.studentsevice.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.studentsevice.dto.EnrollmentRequest;
import ra.studentsevice.dto.EnrollmentResponse;
import ra.studentsevice.service.StudentService;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{studentId}")
    public ResponseEntity<?> getStudent(@PathVariable Long studentId) {
        try {
            return ResponseEntity.ok(studentService.getById(studentId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    @PostMapping("/enroll")
    public ResponseEntity<?> enroll(@RequestBody EnrollmentRequest request) {
        try {
            studentService.enroll(request.studentId(), request.courseId());
            return ResponseEntity.ok(new EnrollmentResponse("dang ki thanh cong ", request.studentId(), request.courseId()));
        } catch (FeignException.NotFound ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("khong tim thay khoa hoc ");
        } catch (FeignException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("khong lay duoc khoa hoc tu service");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }



}
