package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private ConfigurableApplicationContext appCtx;

    private MealRestController controller;

    @Override
    public void init() {
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        controller = appCtx.getBean(MealRestController.class);
    }

    @Override
    public void destroy() {
        appCtx.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String stringId = request.getParameter("id");
        Integer id = stringId.isEmpty() ? null : Integer.valueOf(stringId);
        Meal meal = new Meal(id, LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"), Integer.parseInt(request.getParameter("calories")));
        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        if (meal.isNew()) {
            controller.create(meal);
        } else {
            controller.update(meal, id);
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete id={}", id);
                controller.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                                "", 1000) :
                        controller.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "filter":
                String startDateString = request.getParameter("startDate");
                String endDateString = request.getParameter("endDate");
                String startTimeString = request.getParameter("startTime");
                String endTimeString = request.getParameter("endTime");
                if (startDateString.isEmpty() && endDateString.isEmpty() && startTimeString.isEmpty() && endTimeString.isEmpty()) {
                    request.setAttribute("meals", controller.getAll());
                    request.getRequestDispatcher("/meals.jsp").forward(request, response);
                    break;
                }
                LocalDate startDate = startDateString.isEmpty() ? LocalDate.MIN : LocalDate.parse(startDateString);
                LocalDate endDate = endDateString.isEmpty() ? LocalDate.MAX : LocalDate.parse(startDateString);
                LocalTime startTime = startTimeString.isEmpty() ? LocalTime.MIN : LocalTime.parse(startTimeString);
                LocalTime endTime = endTimeString.isEmpty() ? LocalTime.MAX : LocalTime.parse(endTimeString);
                request.setAttribute("meals", controller.getAllFiltered(startDate, endDate, startTime, endTime));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                if (SecurityUtil.authUserId() == 0) {
                    response.sendRedirect("users");
                    break;
                }
                request.setAttribute("meals", controller.getAll());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
