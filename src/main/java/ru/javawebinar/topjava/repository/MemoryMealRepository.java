package ru.javawebinar.topjava.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryMealRepository implements Repository<Meal> {
    private static final Logger log = LoggerFactory.getLogger(MemoryMealRepository.class);

    private static final AtomicInteger mealIdCounter = new AtomicInteger();

    private final Map<Integer, Meal> meals = new ConcurrentHashMap<>();

    public MemoryMealRepository() {
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        save(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    public List<Meal> findAll() {
        log.debug("find all meals");
        return new ArrayList<>(meals.values());
    }

    @Override
    public Optional<Meal> findById(Integer id) {
        log.debug("find meal by id {}", id);
        return Optional.ofNullable(meals.get(id));
    }

    @Override
    public Meal save(Meal meal) {
        meal.setId(mealIdCounter.incrementAndGet());
        log.debug("save meal {}", meal);
        return meals.put(meal.getId(), meal);
    }

    @Override
    public Meal update(Meal meal) {
        Meal oldMeal = meals.get(meal.getId());
        if (oldMeal != null) {
            log.debug("update meal {}", oldMeal);
            oldMeal.setDateTime(meal.getDateTime());
            oldMeal.setDescription(meal.getDescription());
            oldMeal.setCalories(meal.getCalories());
        }
        return oldMeal;
    }

    @Override
    public void delete(Integer id) {
        log.debug("delete meal by id {}", id);
        meals.remove(id);
    }
}
