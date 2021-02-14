package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, List<Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, SecurityUtil.authUserId()));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.merge(userId, new ArrayList<>(Collections.singletonList(meal)), (currentList, newList) -> {
                currentList.addAll(newList);
                return currentList;
            });
            return meal;
        }
        // handle case: update, but not present in storage
        List<Meal> meals = repository.computeIfPresent(userId, (key, list) -> {
            ListIterator<Meal> iterator = list.listIterator();
            while (iterator.hasNext()) {
                if (iterator.next().getId().intValue() == meal.getId()) {
                    iterator.set(meal);
                    return list;
                }
            }
            return null;
        });
        return meals != null ? meal : null;
    }

    @Override
    public boolean delete(int id, int userId) {
        return repository.get(userId).removeIf(meal -> meal.getId() == id);
    }

    @Override
    public Meal get(int id, int userId) {
        return repository.get(userId).stream()
                .filter(meal -> meal.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return repository.entrySet().stream()
                .filter(entry -> entry.getKey() == userId)
                .flatMap(e -> e.getValue().stream())
                .sorted(Comparator.comparing(Meal::getDate, Comparator.reverseOrder())
                        .thenComparing(Meal::getTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAll(int userId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        return getAll(userId).stream()
                .filter(meal ->
                        DateTimeUtil.isBetweenDate(meal.getDate(), startDate, endDate) &&
                                DateTimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .collect(Collectors.toList());
    }
}

