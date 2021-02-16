package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    //for test
    {
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 1500), 1);
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000), 1);
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500), 2);
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 29, 0, 0), "Еда на граничное значение", 100), 1);
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000), 2);
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500), 1);
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410), 1);
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());

            if (repository.containsKey(userId)) {
                repository.get(userId).put(meal.getId(), meal);
            } else {
                Map<Integer, Meal> mealForSaving = new ConcurrentHashMap<>();
                mealForSaving.put(meal.getId(), meal);
                repository.put(userId, mealForSaving);
            }
            //for test
            meal.setDescription(userId + " - " + meal.getDescription());
            return meal;
        }
        return repository.get(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return repository.entrySet().stream()
                .filter(e -> e.getKey() == userId)
                .map(Map.Entry::getValue)
                .map(e -> e.remove(id))
                .anyMatch(Objects::nonNull);
    }

    @Override
    public Meal get(int id, int userId) {
        return repository.entrySet().stream()
                .filter(e -> e.getKey() == userId)
                .map(Map.Entry::getValue)
                .filter(e -> e.containsKey(id))
                .map(e -> e.get(id))
                .findFirst()
                .orElse(null);
    }

    private List<Meal> getForUser(int userId) {
        return repository.entrySet().stream()
                .filter(entry -> entry.getKey() == userId)
                .map(Map.Entry::getValue)
                .map(Map::values)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Meal> filteringAndSortingList(int userId, Predicate<Meal> filter) {
        return getForUser(userId).stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDate, Comparator.reverseOrder())
                        .thenComparing(Meal::getTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAll(int userId) {
        return filteringAndSortingList(userId, meal -> true);
    }

    @Override
    public List<Meal> getAllWithFilter(int userId, LocalDate startDate, LocalDate endDate) {
        LocalDate sd = startDate == null ? LocalDate.MIN : startDate;
        LocalDate ed = endDate == null ? LocalDate.MAX : endDate;
        return filteringAndSortingList(userId, meal -> DateTimeUtil.isBetweenDate(meal.getDate(), sd, ed));
    }
}

