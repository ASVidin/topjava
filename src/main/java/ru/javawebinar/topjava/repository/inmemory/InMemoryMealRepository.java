package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, meal.getUserId()));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        Map<Integer, Meal> collect = repository.entrySet().stream()
                .filter(entry -> entry.getValue().getUserId() == userId)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Meal mealUpdated = collect.computeIfPresent(meal.getId(), (key, oldValue) -> meal);
        repository.putAll(collect);
        return mealUpdated;
    }

    @Override
    public boolean delete(int id, int userId) {
        return repository.values().removeIf(meal -> meal.getUserId() == userId && meal.getId() == id);
    }

    @Override
    public Meal get(int id, int userId) {
        return repository.values().stream()
                .filter(meal -> meal.getId() == id && meal.getUserId() == userId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return repository.values().stream()
                .filter(meal -> meal.getUserId() == userId)
                .sorted(Comparator.comparing(Meal::getDate, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}

