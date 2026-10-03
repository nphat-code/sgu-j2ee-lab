package net.javaguides.springboot.controller;

import net.javaguides.springboot.service.Pizza;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PizzaController {

    private Pizza pizza;

    // Constructor Injection kết hợp @Autowired và @Qualifier để chọn chính xác bean mong muốn
    @Autowired
    public PizzaController(@Qualifier("vegPizza") Pizza pizza) {
        System.out.println("inside PizzaController constructor");
        this.pizza = pizza;
    }

    public String getPizza() {
        return pizza.getPizza();
    }

    public Pizza getPizzaBean() {
        return pizza;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }
}
