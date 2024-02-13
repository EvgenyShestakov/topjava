package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(this::save);
    }

    @Override
    public Meal save(Meal newMeal) {
        if (newMeal.isNew()) {
            newMeal.setId(counter.incrementAndGet());
            repository.put(newMeal.getId(), newMeal);
            return newMeal;
        }
        Meal[] updatedMeal = new Meal[1];
        repository.computeIfPresent(newMeal.getId(), (key, oldMeal) -> {
            if (oldMeal.getUserId().equals(newMeal.getUserId())) {
                updatedMeal[0] = newMeal;
                return newMeal;
            }
            return oldMeal;
        });
        return updatedMeal[0];
    }

    @Override
    public boolean delete(int id, Integer userId) {
        boolean flag = false;
        Meal meal = repository.get(id);
        if (meal != null && meal.getUserId().equals(userId)) {
            repository.remove(id);
            flag = true;
        }
        return flag;
    }

    @Override
    public Meal get(int id, Integer userId) {
        Meal meal = repository.get(id);
        return meal != null && meal.getUserId().equals(userId) ? meal : null;
    }

    @Override
    public List<Meal> getAll(Integer userId) {
        return repository.values().stream()
                .filter(meal -> meal.getUserId().equals(userId))
                .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
                .collect(Collectors.toList());
    }
}

