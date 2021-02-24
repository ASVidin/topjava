package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration({
        "classpath:spring/jdbc-repository.xml",
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
        Meal meal = service.get(MEAL_ID_2, USER_ID);
        assertMatch(meal, userMeal2);
    }

    @Test(expected = NotFoundException.class)
    public void getNotYourMeal() {
        int adminMeal = adminMeal3.getId();
        service.get(adminMeal, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getNotFoundMeal() {
        service.get(NOT_FOUND, USER_ID);
    }

    @Test()
    public void delete() {
        service.delete(MEAL_ID_2, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID_2, USER_ID));
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotYourMeal() {
        int adminMeal = adminMeal3.getId();
        service.delete(adminMeal, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotFoundMeal() {
        service.delete(NOT_FOUND, USER_ID);
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate sd = LocalDate.of(2021, Month.FEBRUARY, 10);
        List<Meal> actualList = service.getBetweenInclusive(sd, null, USER_ID);
        assertEqualsWhitSorted(actualList, mealsOfUserWithFilter);
    }

    @Test
    public void getAll() {
        List<Meal> mealList = service.getAll(USER_ID);
        assertEqualsWhitSorted(mealList, mealsOfUser);
    }

    @Test
    public void update() {
        Meal updated = getUpdatedMeal();
        service.update(updated, USER_ID);
        assertMatch(service.get(MEAL_ID_2, USER_ID), getUpdatedMeal());
    }

    @Test(expected = NotFoundException.class)
    public void updateNotFoundMeal() {
        Meal updated = getUpdatedMeal();
        service.update(updated, NOT_FOUND);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotYourMeal() {
        Meal updated = getUpdatedMeal();
        service.update(updated, ADMIN_ID);
    }

    @Test(expected = DataAccessException.class)
    public void updateDuplicateDateTime() {
        Meal updated = getDuplicateUpdated();
        service.update(updated, USER_ID);
    }

    @Test
    public void create() {
        Meal created = service.create(getNewMeal(), USER_ID);
        Meal newMeal = getNewMeal();
        Integer id = created.getId();
        newMeal.setId(id);
        assertMatch(created, newMeal);
        assertThat(service.get(id, USER_ID)).usingRecursiveComparison().isEqualTo(newMeal);
    }
}