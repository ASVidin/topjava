package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int MEAL_ID = 100_002;
    public static final int USER_ID = START_SEQ;

    public static final Meal meal = new Meal(MEAL_ID,
            LocalDateTime.of(2021, Month.FEBRUARY, 10, 8, 1, 0),
            "Завтрак", 1500);
    public static final Meal meal2 = new Meal(MEAL_ID + 1,
            LocalDateTime.of(2021, Month.FEBRUARY, 10, 8, 1, 0),
            "Завтрак", 1500);
    public static final Meal meal3 = new Meal(MEAL_ID + 2,
            LocalDateTime.of(2021, Month.FEBRUARY, 9, 8, 1, 0),
            "Завтрак", 1500);
    public static final Meal meal4 = new Meal(MEAL_ID + 3,
            LocalDateTime.of(2021, Month.FEBRUARY, 10, 21, 30, 45),
            "Ужин", 500);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2021, Month.FEBRUARY, 11, 8, 1, 0),
                "Завтрак new", 500);
    }

    public static void assertEqualsWhitSorted(Predicate<Meal> filter, List<Meal> actual, Meal... expected) {
        List<Meal> meals = getListWithSortAndFilter(filter, expected);
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(meals);
    }

    private static List<Meal> getListWithSortAndFilter(Predicate<Meal> filter, Meal[] meals) {
        return Stream.of(meals)
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}
