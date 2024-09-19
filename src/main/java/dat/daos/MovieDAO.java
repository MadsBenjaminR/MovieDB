package dat.daos;

import dat.config.HibernateConfig;
import dat.dtos.ActorDTO;
import dat.dtos.DirectorDTO;
import dat.dtos.GenreDTO;
import dat.dtos.MovieDTO;
import dat.entities.Actor;
import dat.entities.Director;
import dat.entities.Genre;
import dat.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Purpose:
 *
 * @author: Jeppe Koch
 */
public class MovieDAO {
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("tmdb");



    public void create(Set<MovieDTO> movieDTOs) {
        EntityManager em = null;
        EntityTransaction transaction = null;

        try {
            em = emf.createEntityManager();
            transaction = em.getTransaction();
            transaction.begin();

            for (MovieDTO movieDTO : movieDTOs) {
                if (movieDTO == null) {
                    System.err.println("Skipping null MovieDTO");
                    continue;
                }

                Movie movie = new Movie(movieDTO);
                em.persist(movie);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    public Set<MovieDTO> establishRelationships(Set<MovieDTO> movieDTOs) {
        EntityManager em = null;
        EntityTransaction transaction = null;

        try {
            em = emf.createEntityManager();
            transaction = em.getTransaction();
            transaction.begin();

            for (MovieDTO movieDTO : movieDTOs) {
                if (movieDTO == null) {
                    System.err.println("Skipping null MovieDTO");
                    continue;
                }

                // Find the movie in the database
                Movie movie = em.find(Movie.class, movieDTO.getId());
                if (movie == null) {
                    System.err.println("Movie not found for ID: " + movieDTO.getId());
                    continue;
                }

                // Handle Directors
                Set<Director> directors = getOrCreateDirectors(em, movieDTO.getDirectors());
                movie.setDirectors(directors);

                // Handle Actors
                Set<Actor> actors = getOrCreateActors(em, movieDTO.getActors());
                movie.setActors(actors);

                // Handle Genres
                Set<Genre> genres = getOrCreateGenres(em, movieDTO.getGenres());
                movie.setGenres(genres);

                // Merge the updated movie entity
                em.merge(movie);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return movieDTOs;
    }

    private Set<Director> getOrCreateDirectors(EntityManager em, Set<DirectorDTO> directorDTOs) {
        Set<Director> directors = new HashSet<>();
        if (directorDTOs != null) {
            for (DirectorDTO dto : directorDTOs) {
                Director director = em.find(Director.class, dto.getId());
                if (director == null) {
                    director = new Director();
                    director.setId(dto.getId());
                    director.setFullName(dto.getName());
                    director.setJob(dto.getJob());
                    em.merge(director);
                }
                directors.add(director);
            }
        }
        return directors;
    }

    private Set<Actor> getOrCreateActors(EntityManager em, Set<ActorDTO> actorDTOs) {
        Set<Actor> actors = new HashSet<>();
        if (actorDTOs != null) {
            for (ActorDTO dto : actorDTOs) {
                Actor actor = em.find(Actor.class, dto.getId());
                if (actor == null) {
                    actor = new Actor();
                    actor.setId(dto.getId());
                    actor.setFullName(dto.getName());
                    em.merge(actor);
                }
                actors.add(actor);
            }
        }
        return actors;
    }

    private Set<Genre> getOrCreateGenres(EntityManager em, Set<GenreDTO> genreDTOs) {
        Set<Genre> genres = new HashSet<>();
        if (genreDTOs != null) {
            for (GenreDTO dto : genreDTOs) {
                Genre genre = em.find(Genre.class, dto.getId());
                if (genre == null) {
                    genre = new Genre();
                    genre.setId((long) dto.getId());
                    genre.setName(dto.getName());
                    em.merge(genre);
                }
                genres.add(genre);
            }
        }
        return genres;
    }
    // TODO: ret til når dto er lavet
    /*
    @Override
    public MovieDTO update(MovieDTO movieDTO) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // Find existing movie by ID
            Movie movie = em.find(Movie.class, movieDTO.getId());
            if (movie == null) {
                throw new IllegalArgumentException("Movie with ID " + movieDTO.getId() + " not found.");
            }

            // Update the movie fields with new DTO values
            movie.setTitle(movieDTO.getTitle()); // Assuming there's a title field, adjust as per your entity

            // Update Director
            Director director = em.find(Director.class, movieDTO.getDirectors().getId());
            if (director != null) {
                movie.setDirectors(director);
            } else {
                throw new IllegalArgumentException("Director with ID " + movieDTO.getDirectors().getId() + " not found.");
            }

            // Update Actor
            Actor actor = em.find(Actor.class, movieDTO.getActors().getId());
            if (actor != null) {
                movie.setActors(actor);
            } else {
                throw new IllegalArgumentException("Actor with ID " + movieDTO.getActors().getId() + " not found.");
            }

            // Update Genre
            Genre genre = em.find(Genre.class, movieDTO.getGenres().getId());
            if (genre != null) {
                movie.setGenres(genre);
            } else {
                throw new IllegalArgumentException("Genre with ID " + movieDTO.getGenres().getId() + " not found.");
            }

            // Merge the updated movie and commit the transaction
            em.merge(movie);
            em.getTransaction().commit();

            // Return the updated MovieDTO
            return new MovieDTO(movie);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Optionally handle the exception better
        } finally {
            em.close();
        }
    }


    @Override
    public void delete(MovieDTO movie) {
        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.remove(movie);
            em.getTransaction().commit();
        }
    }

    @Override
    public MovieDTO findById(MovieDTO movieDTO) {
        return null;
    }

    @Override
    public Movie findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return new MovieDTO(em.find(Movie.class, id));
        }
    }

    @Override
    public List<MovieDTO> findAll() {
        try(EntityManager em = emf.createEntityManager()){
            //vi bruger jpl, hvor man bruger objekter i stedet for sql
          return em.createQuery("select new dtos.MovieDTO(m) FROM movie m", MovieDTO.class).getResultList();
        }
    }

     */

}
