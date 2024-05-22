package com.example.doantotnghiep_xemphim.Fragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MovieApiService {
    @POST("/recommend")
    Call<List<String>> getRecommendations(@Body RecommendationRequest request);
}
