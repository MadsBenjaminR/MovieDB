package dat.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String firstName;
    private String lastName;
    private String fullName;

    @ManyToOne
    private Movie movie;

    public Actor(String firstName, String lastName, Movie movie) {
        this.firstName = firstName;
        this.lastName = lastName;
        fullName = firstName + " "+ lastName;
        this.movie = movie;
    }
}



