package dat;


import dat.daos.MovieDAO;
import dat.dtos.MovieDTO;
import dat.services.JsonService;
import dat.services.MovieService;

import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        MovieService movieService = new MovieService();

        //List<MovieDTO> movies = movieService.getMoviesByName("The Dark Knight");


        movieService.getMovies(3);
    }
}