package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public class MealDao {
    private static DataSource data = DataSource.getInstance();

    public static List<Meal> getAll() {
        return data.getMeals();
    }
}
