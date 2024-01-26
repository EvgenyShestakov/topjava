package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY,
                        30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY,
                        30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY,
                        30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY,
                        31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY,
                        31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY,
                        31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY,
                        31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsByCycles = filteredByCycles(meals, LocalTime.of(7, 0),
                LocalTime.of(12, 0), 2000);
        mealsByCycles.forEach(System.out::println);
        System.out.println();
        List<UserMealWithExcess> mealsStreams = filteredByStreams(meals, LocalTime.of(7, 0),
                LocalTime.of(12, 0), 2000);
        mealsStreams.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(
            List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMeal> filteredUserMeals = new ArrayList<>();
        Map<LocalDate, Integer> totalCaloriesForEachDay = new HashMap<>();
        meals.forEach(userMeal -> {
            LocalDateTime userMealDateTime = userMeal.getDateTime();
            totalCaloriesForEachDay.merge(userMealDateTime.toLocalDate(), userMeal.getCalories(), Integer::sum);
            if (TimeUtil.isBetweenHalfOpen(userMealDateTime.toLocalTime(), startTime, endTime)) {
                filteredUserMeals.add(userMeal);
            }
        });
        List<UserMealWithExcess> userMealWithExcesses = new ArrayList<>();
        filteredUserMeals.forEach(userMeal -> {
            LocalDateTime userMealDateTime = userMeal.getDateTime();
            boolean excess = totalCaloriesForEachDay.get(userMealDateTime.toLocalDate()) > caloriesPerDay;
            UserMealWithExcess userMealWithExcess = new UserMealWithExcess(userMealDateTime,
                    userMeal.getDescription(), userMeal.getCalories(), excess);
            userMealWithExcesses.add(userMealWithExcess);
        });
        return userMealWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(
            List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> totalCaloriesForEachDay = meals.stream()
                .collect(Collectors.groupingBy(userMeal -> userMeal.getDateTime()
                        .toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));
        return meals.stream().filter(userMeal -> TimeUtil.isBetweenHalfOpen(userMeal.getDateTime()
                .toLocalTime(), startTime, endTime)).map(userMeal -> {
            LocalDateTime userMealDateTime = userMeal.getDateTime();
            boolean excess = totalCaloriesForEachDay.get(userMealDateTime.toLocalDate()) > caloriesPerDay;
            return new UserMealWithExcess(userMealDateTime, userMeal.getDescription(), userMeal.getCalories(), excess);
        }).collect(Collectors.toList());
    }
}
