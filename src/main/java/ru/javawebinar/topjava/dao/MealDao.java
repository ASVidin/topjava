package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    List<Meal> getAll();
    Meal getMealById(long id);
    void delete(long id);
    Meal create(Meal meal);
    Meal update(Meal meal);
}
