package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;

public class MealTestData {
    public static final int MEAL_ID_2 = ADMIN_ID + 1;

    public static final Meal MEAL_2_USER = new Meal(MEAL_ID_2, LocalDateTime.of(2021, Month.FEBRUARY, 10, 8, 1, 0), "Завтрак", 1500);
    public static final Meal MEAL_3_ADMIN = new Meal(MEAL_ID_2 + 1, LocalDateTime.of(2021, Month.FEBRUARY, 10, 8, 1, 0), "Завтрак", 1500);
    public static final Meal MEAL_4_USER = new Meal(MEAL_ID_2 + 2, LocalDateTime.of(2021, Month.FEBRUARY, 9, 8, 1, 0), "Завтрак", 1500);
    public static final Meal MEAL_5_USER = new Meal(MEAL_ID_2 + 3, LocalDateTime.of(2021, Month.FEBRUARY, 20, 8, 1, 0), "Завтрак", 100);
    public static final Meal MEAL_6_ADMIN = new Meal(MEAL_ID_2 + 4, LocalDateTime.of(2021, Month.FEBRUARY, 20, 5, 1, 0), "Завтрак", 100);
    public static final Meal MEAL_7_ADMIN = new Meal(MEAL_ID_2 + 5, LocalDateTime.of(2021, Month.FEBRUARY, 2, 8, 1, 0), "Ужин", 100);
    public static final Meal MEAL_8_ADMIN = new Meal(MEAL_ID_2 + 6, LocalDateTime.of(2021, Month.FEBRUARY, 20, 20, 1, 0), "Ужин", 100);
    public static final Meal MEAL_9_USER = new Meal(MEAL_ID_2 + 7, LocalDateTime.of(2021, Month.JANUARY, 2, 18, 1, 0), "Обед", 100);
    public static final Meal MEAL_10_USER = new Meal(MEAL_ID_2 + 8, LocalDateTime.of(2021, Month.FEBRUARY, 2, 8, 1, 0), "Завтрак", 700);
    public static final Meal MEAL_11_USER = new Meal(MEAL_ID_2 + 9, LocalDateTime.of(2021, Month.FEBRUARY, 10, 21, 30, 45), "Ужин", 500);

    public static final List<Meal> MEALS_OF_USER = Arrays.asList(MEAL_2_USER, MEAL_4_USER, MEAL_5_USER, MEAL_9_USER,
            MEAL_10_USER, MEAL_11_USER);

    public static Meal getNewMeal() {
        return new Meal(null, LocalDateTime.of(2021, Month.FEBRUARY, 11, 8, 1, 0),
                "Завтрак new", 500);
    }

    public static Meal getUpdatedMeal() {
        Meal updated = new Meal(MEAL_2_USER);
        updated.setDescription("Ужин");
        updated.setCalories(1);
        return updated;
    }

    public static Meal getDuplicateUpdated() {
        Meal updated = new Meal(MEAL_2_USER);
        updated.setDateTime(LocalDateTime.of(updated.getDate(), LocalTime.of(21, 30, 45)));
        return updated;
    }

    public static void assertEqualsWhitSorted(List<Meal> actual, List<Meal> expected) {
        List<Meal> sortedExpectedMeals = expected.stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(sortedExpectedMeals);
    }

    public static List<Meal> getListWithFilter(Predicate<Meal> filter, List<Meal> meals) {
        return meals.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }
}
