package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
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
                        sumCaloriesPerDay.getOrDefault(dateTimeOfMeal.toLocalDate(), 0) > caloriesPerDay)
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

    public static List<UserMealWithExcess> filteredByStreams3(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .map(userMeal -> new UserMealWithExcess(
                        userMeal.getDateTime(),
                        userMeal.getDescription(),
                        userMeal.getCalories(),
                        meals.stream().collect(Collectors.groupingBy(
                                u -> u.getDateTime().toLocalDate(),
                                Collectors.summingInt(UserMeal::getCalories))).get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay
                ))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByStreams2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return new ArrayList<>(meals.stream()
                .filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                .collect(Collectors.groupingBy(
                        userMeal -> userMeal.getDateTime().toLocalDate(),
                        Collectors.toList()
                        )
                )
                .values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(
                        userMeal -> userMeal.getDateTime().toLocalDate(),
                        UserMealsUtil.partitioningByExcess(caloriesPerDay))
                )
                .values().stream().collect(UserMealsUtil.partitioningUserMealByExcess()));
    }

    private static Collector<UserMeal, ?, Map<Boolean, List<UserMeal>>> partitioningByExcess(int caloriesPerDay) {
        return Collector.<UserMeal, List<UserMeal>, Map<Boolean, List<UserMeal>>>of(
                ArrayList::new,
                List::add,
                (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                },
                c -> {
                    int leftCalories = caloriesPerDay;
                    for (UserMeal userMeal : c) {
                        leftCalories -= userMeal.getCalories();
                    }
                    Map<Boolean, List<UserMeal>> result = new HashMap<>(2);
                    result.put(leftCalories < 0, c);
                    return result;
                }
        );
    }

    private static Collector<Map<Boolean, List<UserMeal>>, ?, List<UserMealWithExcess>> partitioningUserMealByExcess() {
        return Collector.<Map<Boolean, List<UserMeal>>, Map<UserMeal, Boolean>, List<UserMealWithExcess>>of(
                HashMap::new,
                (out, in) -> {
                    for (Map.Entry<Boolean, List<UserMeal>> userMealAndExcess : in.entrySet()) {
                        for (UserMeal userMeal : userMealAndExcess.getValue()) {
                            out.put(userMeal, userMealAndExcess.getKey());
                        }
                    }
                },
                (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                },
                c -> c.entrySet().stream()
                        .map(userMeal -> new UserMealWithExcess(
                                userMeal.getKey().getDateTime(),
                                userMeal.getKey().getDescription(),
                                userMeal.getKey().getCalories(), userMeal.getValue()))
                        .collect(Collectors.toList())
        );
    }

}
