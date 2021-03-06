package com.mealjournal.app.model;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.jni.Local;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

@Entity
@Table(name = "users")
public class User extends Client {

    @NotBlank // doesn't allow null and trimmed-blank values
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String picUrl;

    @Getter
    @Setter
    @Temporal(TemporalType.DATE)
    private Date dob;

    @Getter
    @Setter
    private Integer weight; //supports only kgs now //todo: add support for double type weight

    @Getter
    @Setter
    private Integer height; //supports ony cms now

    @OneToMany(mappedBy = "user", cascade =  CascadeType.ALL) // refers to user attribute of meal class
    @JsonManagedReference
    @Getter
    @Setter
    @OrderBy(value = "meal_date_time asc")
    private Set<Meal> meals = new HashSet<>();

    @Getter
    @Setter
    @Range(min = 100)
    private Integer dailyCalorieGoal;

    public User() {}
    private User(Builder builder) {
        setName(builder.name);
        setPicUrl(builder.picUrl);
        setDob(builder.dob);
        setWeight(builder.weight);
        setHeight(builder.height);
        setDailyCalorieGoal(builder.dailyCalorieGoal);
    }

    //public User() {}
    public static final class Builder {
        private @NotBlank String name;
        private String picUrl;
        private Date dob;
        private Integer weight;
        private Integer height;
        private @Range(min = 100) Integer dailyCalorieGoal;

        public Builder name(@NotBlank String val) {
            name = val;
            return this;
        }

        public Builder picUrl(String val) {
            picUrl = val;
            return this;
        }

        public Builder dob(Date val) {
            dob = val;
            return this;
        }

        public Builder weight(Integer val) {
            weight = val;
            return this;
        }

        public Builder height(Integer val) {
            height = val;
            return this;
        }

        public Builder dailyCalorieGoal(@Range(min = 100) Integer val) {
            dailyCalorieGoal = val;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    // if server and client are in different time zones, there is a rare chance of this giving wrong output
    public Integer calculateAge() {
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate = dob.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        if (dob != null) {
            return Period.between(birthDate, currentDate).getYears();
        }
        return null;
    }

    public void addMeal(Meal meal) {
        if (!this.equals(meal.getUser())) {
            System.out.println("different user"); //todo: throw actual error
            return;
        }
        meals.add(meal);
    }

    public void removeMeal(Meal meal) {
        if (!this.equals(meal.getUser())) {
            System.out.println("different user"); //todo: throw actual error
            return;
        }
        meals.remove(meal);
    }

    //a custom overloaded getter for meals
    public Set<Meal> getMeals(Date startDate, Date stopDate) {
        Set<Meal> queriedMeals = new HashSet<>();
        for (Meal meal: meals) {
            if (meal.getMealDateTime().after(startDate) && meal.getMealDateTime().before(stopDate))
                queriedMeals.add(meal);
        }
        return queriedMeals;
    }

    //todo: calculating mealColor would require creating a MealDate class from which meals extend - think more on this
}
