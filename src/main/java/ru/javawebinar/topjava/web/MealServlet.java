package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MemoryMealRepository;
import ru.javawebinar.topjava.repository.Repository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);

    private static final int CALORIES_PER_DAY = 2000;

    private static final String MEALS = "/meals";

    private static final String MEALS_JSP_PATH = "/meals.jsp";

    private static final String EDIT_JSP_PATH = "/editMeal.jsp";

    private static final String DELETE = "delete";

    private static final String CREATE = "create";

    private static final String UPDATE = "update";

    private static final Set<String> CHECK_ACTION = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(DELETE, UPDATE)));

    private Repository<Meal> repository;

    @Override
    public void init() {
        repository = new MemoryMealRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("meals", MealsUtil
                    .filteredByStreams(repository.findAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
            log.debug("Forwarding to meals.jsp");
            request.getRequestDispatcher(MEALS_JSP_PATH).forward(request, response);
            return;
        }
        String stringMealId;
        int mealId = 0;
        if (CHECK_ACTION.contains(action)) {
            stringMealId = Optional.ofNullable(request.getParameter("id"))
                    .orElseThrow(() -> new IllegalArgumentException("MealId is null"));
            mealId = Integer.parseInt(stringMealId);
        }
        switch (action) {
            case DELETE:
                repository.delete(mealId);
                log.debug("Redirecting to meals.jsp");
                response.sendRedirect(request.getContextPath() + MEALS);
                break;
            case CREATE:
                request.setAttribute("meal", new Meal());
                log.debug("Forwarding to editMeal.jsp");
                request.getRequestDispatcher(EDIT_JSP_PATH).forward(request, response);
                break;
            case UPDATE:
                Meal meal = repository
                        .findById(mealId).orElseThrow(() -> new NoSuchElementException("Meal not found"));
                request.setAttribute("meal", meal);
                log.debug("Forwarding to editMeal.jsp");
                request.getRequestDispatcher(EDIT_JSP_PATH).forward(request, response);
                break;
            default:
                log.debug("Redirecting to meals.jsp");
                response.sendRedirect(request.getContextPath() + MEALS);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String stringId = request.getParameter("id");
        Integer id = stringId.isEmpty() ? null : Integer.valueOf(stringId);
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        Meal meal = new Meal(id, dateTime, description, calories);
        if (id == null) {
            repository.create(meal);
        } else {
            repository.update(meal);
        }
        log.debug("Redirecting to meals.jsp");
        response.sendRedirect(request.getContextPath() + MEALS);
    }
}
