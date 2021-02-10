package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealMemoryDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final int CALORIES_PER_DAY = 2000;
    private static final String INSERT_OR_EDIT = "/editMeal.jsp";
    private static final String LIST_MEALS = "/meals.jsp";
    private static final MealDao MEAL_DAO = new MealMemoryDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String forward;
        String action = req.getParameter("action");
        List<MealTo> meals = MealsUtil.filteredByStreams(MEAL_DAO.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);

        if (action == null) {
            forward = LIST_MEALS;
            req.setAttribute("list", meals);
        } else if (action.equalsIgnoreCase("delete")) {
            long id = Long.parseLong(req.getParameter("mealId"));
            MEAL_DAO.delete(id);
            req.setAttribute("list", meals);
            resp.sendRedirect("meals");
            return;
        } else if (action.equalsIgnoreCase("edit")) {
            forward = INSERT_OR_EDIT;
            int mealId = Integer.parseInt(req.getParameter("mealId"));
            Meal meal = MEAL_DAO.getMealById(mealId);
            req.setAttribute("meal", meal);
        } else {
            forward = INSERT_OR_EDIT;
        }

        log.debug("redirect to meals");
        req.getRequestDispatcher(forward).forward(req, resp);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("datetime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));

        if (request.getParameter("id").isEmpty()) {
            MEAL_DAO.create(new Meal(dateTime, description, calories));
        } else {
            Meal updatedMeal = new Meal(dateTime, description, calories);
            updatedMeal.setId(Long.parseLong(request.getParameter("id")));
            MEAL_DAO.update(updatedMeal);
        }

        List<MealTo> meals = MealsUtil.filteredByStreams(MEAL_DAO.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);

        request.setAttribute("list", meals);
        request.getRequestDispatcher(LIST_MEALS).forward(request, response);
    }
}
