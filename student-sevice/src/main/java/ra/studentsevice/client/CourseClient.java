package ra.studentsevice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import ra.studentsevice.dto.CourseAvailableResponse;
@FeignClient(name = "course-service")
public interface CourseClient {

    @GetMapping("/api/courses/{courseId}/available")
    CourseAvailableResponse getAvailability(@PathVariable Long courseId);

    @PutMapping("/api/courses/{courseId}/enroll")
    void enroll(@PathVariable Long courseId);

}
