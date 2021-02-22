package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.Util;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(MEAL_ID, USER_ID);
        assertThat(meal).usingRecursiveComparison().isEqualTo(MealTestData.meal);
    }

    @Test(expected = NotFoundException.class)
    public void getNotFound() {
        Meal meal = service.get(MEAL_ID + 1, USER_ID);
        assertThat(meal).usingRecursiveComparison().isEqualTo(meal2);
    }

    @Test(expected = NotFoundException.class)
    public void notFoundDelete() {
        service.delete(1, USER_ID);
    }

    @Test()
    public void delete() {
        service.delete(MEAL_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID, USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate sd = LocalDate.of(2021, Month.FEBRUARY, 10);
        List<Meal> mealList = service.getBetweenInclusive(sd, null, USER_ID);
        assertEqualsWhitSorted(meal ->
                        Util.isBetweenHalfOpen(
                                meal.getDateTime(),
                                DateTimeUtil.atStartOfDayOrMin(sd),
                                DateTimeUtil.atStartOfNextDayOrMax(null)),
                mealList, meal, meal3, meal4);
    }

    @Test
    public void getAll() {
        List<Meal> mealList = service.getAll(USER_ID);
        assertEqualsWhitSorted(meal -> true, mealList, meal, meal3, meal4);
    }

    @Test
    public void update() {
        Meal updated = new Meal(meal);
        updated.setDescription("Ужин");
        updated.setCalories(1);
        service.update(updated, USER_ID);
        assertThat(updated).usingRecursiveComparison().isEqualTo(service.get(MEAL_ID, USER_ID));
    }

    @Test(expected = DataAccessException.class)
    public void duplicateDateTimeUpdate() {
        Meal updated = new Meal(meal);
        updated.setDateTime(LocalDateTime.of(updated.getDate(), LocalTime.of(21, 30, 45)));
        service.update(updated, USER_ID);
        assertThat(updated).usingRecursiveComparison().isEqualTo(service.get(MEAL_ID, USER_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Meal newMeal = getNew();
        Integer id = created.getId();
        newMeal.setId(id);
        assertThat(created).usingRecursiveComparison().isEqualTo(newMeal);
        assertThat(service.get(id, USER_ID)).usingRecursiveComparison().isEqualTo(newMeal);
    }
}