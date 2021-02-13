package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.ArrayList;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll(int userId) {
        log.info("getAll for userId={}", userId);
        ArrayList<Meal> meals = new ArrayList<>(service.getAll(userId));
        return MealsUtil.getTos(meals, SecurityUtil.authUserCaloriesPerDay());
    }

    public Meal get(int id, int userId) {
        log.info("get id={} for userId={}", id, userId);
        return service.get(id, userId);
    }

    public Meal create(Meal meal) {
        log.info("create meal {} for userId={}", meal, meal.getUserId());
        checkNew(meal);
        return service.create(meal, meal.getUserId());
    }

    public void delete(int id, int userId) {
        log.info("delete id={} for userId={}", id, userId);
        service.delete(id, userId);
    }

    public void update(Meal meal, int userId) {
        log.info("update meal {} for userId={}", meal, userId);
        assureIdConsistent(meal, meal.getId());
        service.update(meal, userId);
    }
}