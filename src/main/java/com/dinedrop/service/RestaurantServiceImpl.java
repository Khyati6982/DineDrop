package com.dinedrop.service;

import com.dinedrop.model.Restaurant;
import com.dinedrop.repository.RestaurantRepository;
import com.dinedrop.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public void saveRestaurant(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id).orElse(null);
    }

    @Override
    public void updateRestaurant(Long id, Restaurant updated) {
        Restaurant existing = getRestaurantById(id);
        if (existing != null) {
            existing.setName(updated.getName());
            existing.setCuisine(updated.getCuisine());
            existing.setLocation(updated.getLocation());
            restaurantRepository.save(existing);
        }
    }

    @Override
    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }
}
