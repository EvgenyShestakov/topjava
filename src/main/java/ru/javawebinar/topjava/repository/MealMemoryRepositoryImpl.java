package ru.javawebinar.topjava.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MealMemoryRepositoryImpl implements Repository<Meal, Integer> {
    private static final Logger log = LoggerFactory.getLogger(MealMemoryRepositoryImpl.class.getName());

    private static final AtomicInteger MEAL_ID = new AtomicInteger(8);

    private final Map<Integer, Meal> meals = new ConcurrentHashMap<>();

    public static Repository<Meal, Integer> instOf() {
        return Lazy.INST;
    }

    private static final class Lazy {
        private static final Repository<Meal, Integer> INST = new MealMemoryRepositoryImpl();
    }

    private MealMemoryRepositoryImpl() {
        meals.put(1, new Meal(1, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        meals.put(2, new Meal(2, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        meals.put(3, new Meal(3, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        meals.put(4, new Meal(4, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        meals.put(5, new Meal(5, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        meals.put(6, new Meal(6, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        meals.put(7, new Meal(7, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    public Collection<Meal> findAll() {
        log.debug("find all meals");
        return meals.values();
    }

    @Override
    public Optional<Meal> findById(Integer id) {
        log.debug("find meal by id {}", id);
        return Optional.ofNullable(meals.get(id));
    }

    @Override
    public void save(Meal meal) {
        if (meal.getId() == 0) {
            meal.setId(MEAL_ID.incrementAndGet());
        }
        log.debug("save meal {}", meal);
        meals.put(meal.getId(), meal);
    }

    @Override
    public void delete(Integer id) {
        log.debug("delete meal by id {}", id);
        meals.remove(id);
    }
}
