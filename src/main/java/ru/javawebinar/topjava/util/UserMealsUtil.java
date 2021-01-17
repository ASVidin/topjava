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
        List<UserMeal> filteredUserMeals = new ArrayList<>();
        List<UserMealWithExcess> userMealsWithExcess = new ArrayList<>();
        Map<LocalDate, Integer> sumCaloriesPerDay = new HashMap<>();

        for (UserMeal userMeal : meals) {
            LocalDateTime dateTimeOfMeal = userMeal.getDateTime();
            sumCaloriesPerDay.merge(dateTimeOfMeal.toLocalDate(), userMeal.getCalories(), Integer::sum);

            if (TimeUtil.isBetweenHalfOpen(dateTimeOfMeal.toLocalTime(), startTime, endTime)) {
                filteredUserMeals.add(new UserMeal(dateTimeOfMeal, userMeal.getDescription(), userMeal.getCalories()));
            }
        }

        for (UserMeal userMeal : filteredUserMeals) {
            LocalDateTime dateTimeOfMeal = userMeal.getDateTime();
            boolean excess = false;
            if ((dateTimeOfMeal.toLocalTime().compareTo(startTime) >= 0) && dateTimeOfMeal.toLocalTime().isBefore(endTime)) {
                if (sumCaloriesPerDay.getOrDefault(dateTimeOfMeal.toLocalDate(), 0) > caloriesPerDay) {
                    excess = true;
                }
                userMealsWithExcess.add(new UserMealWithExcess(dateTimeOfMeal, userMeal.getDescription(), userMeal.getCalories(), excess));
            }
        }

        return userMealsWithExcess;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        return meals.stream()
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
                .values().stream().collect(UserMealsUtil.partitioningUserMealByExcess()).stream()
                .filter(mealWithExcess -> TimeUtil.isBetweenHalfOpen(mealWithExcess.getDateTime().toLocalTime(), startTime, endTime))
                .collect(Collectors.toList());
    }

    public static Collector<UserMeal, ?, Map<Boolean, List<UserMeal>>> partitioningByExcess(int caloriesPerDay) {
        return Collector.<UserMeal, List<UserMeal>, Map<Boolean, List<UserMeal>>>of(
                ArrayList::new,
                List::add,
                (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                },
                c -> {
                    Map<Boolean, List<UserMeal>> result = new HashMap<>(2);
                    int leftCalories = caloriesPerDay;
                    for (UserMeal userMeal : c) {
                        leftCalories -= userMeal.getCalories();
                    }
                    if (leftCalories < 0) {
                        result.put(Boolean.TRUE, c);
                    } else
                        result.put(Boolean.FALSE, c);

                    return result;
                }
        );
    }

    public static Collector<Map<Boolean, List<UserMeal>>, ?, List<UserMealWithExcess>> partitioningUserMealByExcess() {
        return Collector.<Map<Boolean, List<UserMeal>>, Map<UserMeal, Boolean>, List<UserMealWithExcess>>of(
                HashMap::new,
                (in, out) -> {
                    for (Map.Entry<Boolean, List<UserMeal>> userMealAndExcess : out.entrySet()) {
                        for (UserMeal userMeal : userMealAndExcess.getValue()) {
                            in.put(userMeal, userMealAndExcess.getKey());
                        }
                    }
                },
                (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                },
                c -> {
                    List<UserMealWithExcess> result = new ArrayList<>();
                    for (Map.Entry<UserMeal, Boolean> pair : c.entrySet()) {
                        UserMeal userMeal = pair.getKey();
                        result.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), pair.getValue()));
                    }

                    return result;
                }
        );
    }

}
