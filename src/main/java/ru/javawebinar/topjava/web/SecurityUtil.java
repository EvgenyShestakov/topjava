package ru.javawebinar.topjava.web;

import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;

public class SecurityUtil {

    public static int USER_ID = 0;

    public static int authUserId() {
        return USER_ID;
    }

    public static void setAuthUserId(int userId) {
        USER_ID = userId;
    }

    public static int authUserCaloriesPerDay() {
        return DEFAULT_CALORIES_PER_DAY;
    }
}