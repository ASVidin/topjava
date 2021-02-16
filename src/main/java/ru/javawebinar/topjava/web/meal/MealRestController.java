package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.getAuthUserId;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        log.info("getAll for userId={}", getAuthUserId());
        List<Meal> meals = new ArrayList<>(service.getAll(getAuthUserId()));
        return MealsUtil.getTos(meals, authUserCaloriesPerDay());
    }

    public List<MealTo> getAllWhitFilter(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getAll for userId={}, {} {} - {} {}", getAuthUserId(), startDate, startTime, endDate, endTime);
        List<Meal> meals = new ArrayList<>(service.getAllWithFilter(getAuthUserId(), startDate, endDate));
        LocalTime st = startTime == null ? LocalTime.MIN : startTime;
        LocalTime et = endTime == null ? LocalTime.MAX : endTime;
        return MealsUtil.getFilteredTos(meals, authUserCaloriesPerDay(), st, et);
    }

    public Meal get(int id) {
        log.info("get id={} for userId={}", id, getAuthUserId());
        return service.get(id, getAuthUserId());
    }

    public Meal create(Meal meal) {
        log.info("create meal {} for userId={}", meal, getAuthUserId());
        checkNew(meal);
        return service.create(meal, getAuthUserId());
    }

    public void delete(int id) {
        log.info("delete id={} for userId={}", id, getAuthUserId());
        service.delete(id, getAuthUserId());
    }

    public void update(Meal meal, int id) {
        log.info("update meal {} with id={} for userId={}", meal, id, getAuthUserId());
        assureIdConsistent(meal, id);
        service.update(meal, getAuthUserId());
    }
}