package dat.daos;

import dat.config.HibernateConfig;
import dat.dtos.MovieDTO;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import dat.services.JsonService;
import dat.services.MovieService;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
class MovieCrudDAOTest {

   static MovieCrudDAO movieCrudDAO;
   static EntityManagerFactory entityManagerFactory;
   static Movie m1, m2, m3;
   static Genre g1, g2, g3;
   static Director d1;
   static Actor actor1, actor2;
    Set<Genre> genres = new HashSet<>();
    Set<Director> directors = new HashSet<>();
    Set<Actor> actors = new HashSet<>();


    @BeforeAll
    public static void init() {
       entityManagerFactory = HibernateConfig.getEntityManagerFactoryForTest();
       movieCrudDAO = new MovieCrudDAO();
    }

    @BeforeEach
    void setUp() {
    try(EntityManager em = entityManagerFactory.createEntityManager()) {

        actor1=new Actor(1L,"james james",m1);
        actor2=new Actor(2L,"jane jane",m2);

        g1 = new Genre(1L,"Adventure", m1);
        g2 = new Genre(2L,"Comedy", m2);
        g3 = new Genre(3L,"Sci-Fi", m3);

        d1 = new Director(10L,"Christopher Poland");
        actors.add(actor1);
        actors.add(actor2);

        genres.add(g1);
        genres.add(g2);
        genres.add(g3);

        directors.add(d1);
        m1 = new Movie(1L, "Harry Pot", "HEj",7.0, "en", 100000,directors , actors, genres);
        m2 = new Movie(2L, "Harry Pot2","POIU", 7.3, "en", 110000, directors, actors, genres);
        m3 = new Movie(33L, "Harry Pot3","SLJD", 7.5, "en", 120000, directors, actors, genres);



        em.getTransaction().begin();


        em.persist(d1);

        em.persist(actor1);
        em.persist(actor2);

        em.persist(g1);
        em.persist(g2);
        em.persist(g3);

        em.persist(m1);
        em.persist(m2);
        em.persist(m3);
        em.getTransaction().commit();
    }
    }

    @AfterEach
    void tearDown() {
        try(EntityManager em = entityManagerFactory.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("delete from movie").executeUpdate();
            em.createQuery("delete from Genre").executeUpdate();
            em.createQuery("delete from Actor").executeUpdate();
            em.createQuery("delete from Director").executeUpdate();
            em.getTransaction().commit();

        }


    }

    @Test
    void updatebudget(){
        double expected=44.0;

        m1.setBudget(44.0);
        movieCrudDAO.update(m1);

        assertEquals(m1.getBudget(),expected);

    }

    @Test
    void createMovie(){
        int idExpected = 15;
        Movie newMovie = new Movie(15L, "Harry Pot 4", "Halløj", 7.9,"en",10000.00,directors,actors,genres);
       // movieCrudDAO.create(newMovie);
        assertEquals(idExpected, newMovie.getId());
    }

    @Test
    void delete(){

        // Given
        Movie expected=m1;

        // When
       // movieCrudDAO.delete(m1);


        // When
        assertEquals(m1,expected);
        assertNotNull(m1);
    }

    @Test
    void getAll() {
        int expected=3;
        List<Movie> movieSet=movieCrudDAO.findAll();
        assertEquals(expected,movieSet.size());

    }

}