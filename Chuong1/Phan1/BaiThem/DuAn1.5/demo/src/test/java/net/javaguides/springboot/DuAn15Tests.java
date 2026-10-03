package net.javaguides.springboot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import net.javaguides.springboot.controller.PizzaController;
import net.javaguides.springboot.service.NonVegPizza;
import net.javaguides.springboot.service.Pizza;
import net.javaguides.springboot.service.VegPizza;

@SpringBootTest
class DuAn15Tests {

    @Autowired
    private PizzaController pizzaController;

    @Autowired
    @Qualifier("vegPizza")
    private Pizza vegPizza;

    @Autowired
    @Qualifier("nonVegPizza")
    private Pizza nonVegPizza;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(pizzaController);
        assertNotNull(vegPizza);
        assertNotNull(nonVegPizza);
    }

    @Test
    void testQualifierInjectsVegPizza() {
        assertEquals("Veg Pizza", pizzaController.getPizza());
        assertInstanceOf(VegPizza.class, pizzaController.getPizzaBean());
    }

    @Test
    void testBothBeansExistInContext() {
        Pizza bean1 = applicationContext.getBean("vegPizza", Pizza.class);
        Pizza bean2 = applicationContext.getBean("nonVegPizza", Pizza.class);

        assertEquals("Veg Pizza", bean1.getPizza());
        assertEquals("Non-veg Pizza", bean2.getPizza());
    }
}
