package dat.daos;

import dat.config.HibernateConfig;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MovieCrudDAOTest {
    static EntityManagerFactory emf;
    static Movie movie1, movie2, movie3, movie4;
    static Actor actor1, actor2, actor3, actor4;
    static Director director1, director2, director3, director4;
    static Genre genre1, genre2, genre3, genre4;
    static MovieCrudDAO movieCrudDAO;




    @BeforeAll
    static void setup(){
        emf= HibernateConfig.getEntityManagerFactoryForTest();
    }

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();  // Start the transaction

            // Initialize test data
            actor1 = new Actor();
            actor1.setId(12121L);
            actor1.setFullName("Test Actor");

            director1 = new Director();
            director1.setId(21l);
            director1.setFullName("Test Director");

            genre1 = new Genre();
            genre1.setId(12l);
            genre1.setName("Test Genre");

            // Initialize movie
            movie1 = Movie.builder().id(12121l)
                    .title("Test Movie")
                    .budget(100000.0)  // Initial budget
                    .actors(Set.of(actor1))  // Set actors
                    .directors(Set.of(director1))  // Set directors
                    .genres(Set.of(genre1))  // Set genres
                    .build();

            // Persist entities
            em.persist(actor1);
            em.persist(director1);
            em.persist(genre1);
            em.persist(movie1);

            em.getTransaction().commit();



        }
    }

    @AfterEach
    void tearDown() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM movie ").executeUpdate();
            em.createQuery("DELETE FROM Genre").executeUpdate();
            em.createQuery("DELETE FROM Actor").executeUpdate();
            em.createQuery("DELETE FROM Director").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @Test
    void delete() {
    }

    @Test
    void findById() {
    }

    @Test
    void findAll() {

    }

    @Test
    void updateBudget() {
        // Given: Initial budget is set in setUp()

        // New budget to update
        Double newBudget = 240000.0;

        // Then: Assert that the budget was updated
        assertEquals(newBudget, movieCrudDAO.findById(movie1.getId()).getBudget());
    }
}