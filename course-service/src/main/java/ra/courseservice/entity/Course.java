package ra.courseservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    private int capacity;
    private int credits;

    @Column(name = "enrolled_count")
    private int enrolledCount;

    public boolean CheckAvailableSeat() {
        return enrolledCount < capacity;
    }


}
