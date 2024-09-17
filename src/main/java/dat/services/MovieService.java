package dat.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MovieService {

    private final String apiKey = System.getenv("API_KEY");
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<MovieDTO> getMoviesByName(String movieName) {
        List<MovieDTO> movies = null;
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key is not set.");
        }


        try {
            String encodedMovieTitle = URLEncoder.encode(movieName, StandardCharsets.UTF_8);
            String url = "https://api.themoviedb.org/3/search/movie?query=" + encodedMovieTitle
                    + "&include_adult=false&language=en-US&page=1&api_key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonResponse = response.body();
                MovieApiResponseDTO apiResponse = mapper.readValue(jsonResponse, MovieApiResponseDTO.class);
                movies = apiResponse.getMovies();

                if (movies != null && !movies.isEmpty()) {
                    movies.forEach(movie -> System.out.println("Movie: " + movie));
                    return movies;
                } else {
                    System.out.println("No movies found.");
                }
            } else {
                System.err.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.err.println("Error during API request: " + e.getMessage());
            e.printStackTrace();
        }
        return movies;
    }

    public MovieDTO getMovieByID(int movieID) {
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key is not set man.");
        }

        try {

            String url = "https://api.themoviedb.org/3/movie/" + movieID + "?language=en-US&api_key=" + apiKey;

            // Create a request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the status code and print the response
            if (response.statusCode() == 200) {
                String jsonResponse = response.body();
                MovieDTO movie = mapper.readValue(jsonResponse, MovieDTO.class);
                System.out.println(movie);
                return movie;
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to fetch movies between a specified rating range (e.g., 8.5 to 9.0)
    public List<MovieDTO> getMoviesByRatingRange(double minRating, double maxRating) {
        List<MovieDTO> movies = new ArrayList<>();
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("API key is not set.");
            return movies;
        }

        int currentPage = 1;
        boolean morePagesAvailable = true;
        int totalNumberOfPages = 3;

        while (morePagesAvailable) {
            try {
                String url = "https://api.themoviedb.org/" + totalNumberOfPages + "/discover/movie?api_key=" + apiKey +
                        "&sort_by=vote_average.desc&include_adult=false&include_video=false" +
                        "&vote_average.gte=" + minRating +
                        "&vote_average.lte=" + maxRating +
                        "&page=" + currentPage +
                        "&language=en-US";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String jsonResponse = response.body();
                    MovieApiResponseDTO apiResponse = mapper.readValue(jsonResponse, MovieApiResponseDTO.class);
                    movies = apiResponse.getMovies();
                    if (currentPage >= totalNumberOfPages) {
                        morePagesAvailable = false;
                    } else {
                        currentPage++;
                    }
                } else {
                    System.err.println("GET request failed. Status code: " + response.statusCode());
                    morePagesAvailable = false;
                }
            } catch (URISyntaxException | IOException | InterruptedException e) {
                System.err.println("Error during API request: " + e.getMessage());
                e.printStackTrace();
                morePagesAvailable = false;
            }
        }

        if (movies.isEmpty()) {
            System.out.println("No movies found in the specified rating range.");
        } else {
            movies.forEach(movie -> System.out.println("Movie: " + movie));
        }

        return movies;
    }

    public List<MovieDTO> getSortedByReleaseDate(String query) {
        try {
            // Construct the search query URL
            String url = "https://api.themoviedb.org/3/search/movie?query=" + query +
                    "&include_adult=false&language=en-US&page=1&api_key=" + apiKey;

            // Create an HttpClient instance
            HttpClient client = HttpClient.newHttpClient();

            // Create an HttpRequest
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the request was successful
            if (response.statusCode() == 200) {
                // Deserialize the response into MovieResponseDTO
                MovieApiResponseDTO apiResponse = mapper.readValue(response.body(), MovieApiResponseDTO.class);

                // Get the list of movies
                List<MovieDTO> movies = apiResponse.getMovies();

                // Sort movies by release date in descending order using Java Streams
                return movies.stream()
                        .filter(movie -> movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty())
                        .sorted((movie1, movie2) -> {
                            LocalDate date1 = LocalDate.parse(movie1.getReleaseDate());
                            LocalDate date2 = LocalDate.parse(movie2.getReleaseDate());
                            return date2.compareTo(date1); // Sort in descending order
                        })
                        .collect(Collectors.toList());
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("The method had returned an empty list");
        return List.of(); // Return an empty list if there's an error
    }

}

