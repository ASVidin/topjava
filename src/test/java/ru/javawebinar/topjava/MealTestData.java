package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;

public class MealTestData {
    public static final int MEAL_ID_2 = ADMIN_ID + 1;

    public static final Meal userMeal2 = new Meal(MEAL_ID_2, LocalDateTime.of(2021, Month.FEBRUARY, 10, 8, 1, 0), "Завтрак", 1500);
    public static final Meal adminMeal3 = new Meal(MEAL_ID_2 + 1, LocalDateTime.of(2021, Month.FEBRUARY, 10, 8, 1, 0), "Завтрак", 1500);
    public static final Meal userMeal4 = new Meal(MEAL_ID_2 + 2, LocalDateTime.of(2021, Month.FEBRUARY, 9, 8, 1, 0), "Завтрак", 1500);
    public static final Meal userMeal5 = new Meal(MEAL_ID_2 + 3, LocalDateTime.of(2021, Month.FEBRUARY, 20, 8, 1, 0), "Завтрак", 100);
    public static final Meal adminMeal6 = new Meal(MEAL_ID_2 + 4, LocalDateTime.of(2021, Month.FEBRUARY, 20, 5, 1, 0), "Завтрак", 100);
    public static final Meal adminMeal7 = new Meal(MEAL_ID_2 + 5, LocalDateTime.of(2021, Month.FEBRUARY, 2, 8, 1, 0), "Ужин", 100);
    public static final Meal adminMeal8 = new Meal(MEAL_ID_2 + 6, LocalDateTime.of(2021, Month.FEBRUARY, 20, 20, 1, 0), "Ужин", 100);
    public static final Meal userMeal9 = new Meal(MEAL_ID_2 + 7, LocalDateTime.of(2021, Month.JANUARY, 2, 18, 1, 0), "Обед", 100);
    public static final Meal userMeal10 = new Meal(MEAL_ID_2 + 8, LocalDateTime.of(2021, Month.FEBRUARY, 2, 8, 1, 0), "Завтрак", 700);
    public static final Meal userMeal11 = new Meal(MEAL_ID_2 + 9, LocalDateTime.of(2021, Month.FEBRUARY, 10, 21, 30, 45), "Ужин", 500);

    public static final List<Meal> mealsOfUser = Arrays.asList(userMeal5, userMeal11, userMeal2, userMeal4, userMeal10, userMeal9);

    public static final List<Meal> mealsOfUserWithFilter = Arrays.asList(userMeal5, userMeal11, userMeal2);

    public static Meal getNewMeal() {
        return new Meal(null, LocalDateTime.of(2021, Month.FEBRUARY, 11, 8, 1, 0),
                "Завтрак new", 500);
    }

    public static Meal getUpdatedMeal() {
        Meal updated = new Meal(userMeal2);
        updated.setDescription("Ужин");
        updated.setCalories(1);
        return updated;
    }

    public static Meal getDuplicateUpdated() {
        Meal updated = new Meal(userMeal2);
        updated.setDateTime(userMeal4.getDateTime());
        return updated;
    }

    public static void assertEqualsWhitSorted(List<Meal> actual, List<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
