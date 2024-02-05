package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealMemoryRepositoryImpl;
import ru.javawebinar.topjava.repository.Repository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.Constants.CALORIES_PER_DAY;
import static ru.javawebinar.topjava.util.Constants.DELETE_ACTION;
import static ru.javawebinar.topjava.util.Constants.EDIT_JSP_PATH;
import static ru.javawebinar.topjava.util.Constants.MEALS;
import static ru.javawebinar.topjava.util.Constants.MEALS_JSP_PATH;

public class MealServlet extends HttpServlet {
    private final Repository<Meal, Integer> repository = MealMemoryRepositoryImpl.instOf();

    private static final Logger log = getLogger(MealServlet.class);


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String stringMealId = request.getParameter("id");
        if (stringMealId == null) {
            request.setAttribute("meals", MealsUtil
                    .filteredByStreams(repository.findAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
            log.debug("Forwarding to meals.jsp");
            request.getRequestDispatcher(MEALS_JSP_PATH).forward(request, response);
            return;
        }
        Integer mealId = Integer.valueOf(stringMealId);
        if (DELETE_ACTION.equals(request.getParameter("action"))) {
            repository.delete(mealId);
            log.debug("Redirecting to meals.jsp");
            response.sendRedirect(request.getContextPath() + MEALS);
            return;
        }
        Meal meal = repository.findById(mealId).orElseGet(() -> {
            Meal mealWithIdEqZero = new Meal();
            mealWithIdEqZero.setId(mealId);
            return mealWithIdEqZero;
        });
        request.setAttribute("meal", MealsUtil.createTo(meal));
        log.debug("Forwarding to edit.jsp");
        request.getRequestDispatcher(EDIT_JSP_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("forward to meals.jsp");
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        String dateTime = request.getParameter("dateTime");
        String description = request.getParameter("description");
        String calories = request.getParameter("calories");
        Meal meal = new Meal(Integer.valueOf(id), LocalDateTime.parse(dateTime), description, Integer.parseInt(calories));
        repository.save(meal);
        log.debug("Redirecting to meals.jsp");
        response.sendRedirect(request.getContextPath() + MEALS);
    }
}
