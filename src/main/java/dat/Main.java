package dat;


import dat.daos.MovieDAO;
import dat.dtos.MovieDTO;
import dat.services.JsonService;
import dat.services.MovieService;

import java.util.List;
import java.util.Set;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        MovieService movieService = new MovieService();
        MovieDTO movieDTO = new MovieDTO();
        MovieDAO movieDAO = new MovieDAO();

        Set<MovieDTO> movieDTOS = movieService.getMovies(5);


        movieDAO.create(movieDTOS);
        Set<MovieDTO> movieDTO1=movieDAO.establishRelationships(movieDTOS);


    }
}