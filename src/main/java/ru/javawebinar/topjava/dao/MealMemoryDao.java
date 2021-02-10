package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MealMemoryDao implements MealDao {
    private static final AtomicLong id = new AtomicLong(0);
    private static final Map<Long, Meal> meals = new ConcurrentHashMap<>();

    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal getMealById(long id) {
        return meals.get(id);
    }

    @Override
    public void delete(long id) {
        meals.remove(id);
    }

    @Override
    public Meal create(Meal meal) {
        meal.setId(id.incrementAndGet());
        return meals.put(meal.getId(), meal);
    }

    @Override
    public Meal update(Meal meal) {
        return meals.put(meal.getId(), meal);
    }

}
