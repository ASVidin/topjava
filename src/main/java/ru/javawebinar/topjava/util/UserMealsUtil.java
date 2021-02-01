package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> sumCaloriesPerDay = new HashMap<>();
        for (UserMeal userMeal : meals) {
            LocalDateTime dateTimeOfMeal = userMeal.getDateTime();
            sumCaloriesPerDay.merge(dateTimeOfMeal.toLocalDate(), userMeal.getCalories(), Integer::sum);
        }

        List<UserMealWithExcess> userMealsWithExcess = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            LocalDateTime dateTimeOfMeal = userMeal.getDateTime();
            if (TimeUtil.isBetweenHalfOpen(dateTimeOfMeal.toLocalTime(), startTime, endTime)) {
                userMealsWithExcess.add(new UserMealWithExcess(
                        dateTimeOfMeal,
                        userMeal.getDescription(),
                        userMeal.getCalories(),
                        sumCaloriesPerDay.get(dateTimeOfMeal.toLocalDate()) > caloriesPerDay)
                );
            }
        }
        return userMealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> sumCaloriesPerDay = meals.stream().collect(Collectors.groupingBy(
                userMeal -> userMeal.getDateTime().toLocalDate(),
                Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal -> new UserMealWithExcess(
                        userMeal.getDateTime(),
                        userMeal.getDescription(),
                        userMeal.getCalories(), sumCaloriesPerDay.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByStreams2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .collect(Collectors.groupingBy(
                        userMeal -> userMeal.getDateTime().toLocalDate(),
                        Collectors.mapping(Function.identity(), Collectors.toList())))
                .entrySet().stream()
                .collect(Collectors.partitioningBy(entry -> entry.getValue().stream().map(UserMeal::getCalories).reduce(caloriesPerDay, (a, b) -> a - b) < 0))
                .entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                                .flatMap(map -> map.getValue().stream()
                                        .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                                        .map(userMeal -> new UserMealWithExcess(
                                                userMeal.getDateTime(),
                                                userMeal.getDescription(),
                                                userMeal.getCalories(),
                                                entry.getKey())
                                        )
                                )
                )
                .collect(Collectors.toList());
    }
}
