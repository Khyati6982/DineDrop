package com.dinedrop.service;

import com.dinedrop.model.Restaurant;
import java.util.List;

public interface RestaurantService {

    List<Restaurant> getAllRestaurants();

    void saveRestaurant(Restaurant restaurant);

    Restaurant getRestaurantById(Long id);

    void updateRestaurant(Long id, Restaurant updated);

    void deleteRestaurant(Long id);
}
