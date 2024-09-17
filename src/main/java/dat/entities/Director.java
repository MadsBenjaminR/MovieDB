package dat.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String firstName;
    private String lastName;
    private String fullName;

    @ManyToOne
    private Movie movie;



    public Director(String firstName, String lastName, Movie movie) {
        this.firstName = firstName;
        this.lastName = lastName;
        fullName = firstName + " "+ lastName;
        this.movie = movie;
    }

}
