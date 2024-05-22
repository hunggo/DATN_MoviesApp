package com.example.doantotnghiep_xemphim.Fragment;

import java.util.List;
import java.util.Map;

public class RecommendationRequest {
    private String user_id;
    private Map<String, Object> dbTHMovies;
    private List<String> users;
    private List<String> movies;

    public RecommendationRequest(String user_id, Map<String, Object> dbTHMovies, List<String> users, List<String> movies) {
        this.user_id = user_id;
        this.dbTHMovies = dbTHMovies;
        this.users = users;
        this.movies = movies;
    }

    // Getters and setters
}
