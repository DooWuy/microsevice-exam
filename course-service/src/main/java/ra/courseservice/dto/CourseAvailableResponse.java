package ra.courseservice.dto;

public record CourseAvailableResponse
        (

                Long courseId,
                boolean available,
                int capacity,
                int enrolledCount


        ) {

}
