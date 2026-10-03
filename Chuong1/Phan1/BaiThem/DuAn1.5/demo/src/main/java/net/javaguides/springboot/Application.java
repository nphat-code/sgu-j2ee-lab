package net.javaguides.springboot;

import net.javaguides.springboot.controller.PizzaController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        var context = SpringApplication.run(Application.class, args);
        System.out.println("calling pizzaController.getPizza()");
        PizzaController pizzaController = context.getBean(PizzaController.class);
        String message = pizzaController.getPizza();
        System.out.println(message);
    }
}
