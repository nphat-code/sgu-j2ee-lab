package net.guides.springboot2.springpropertysourceexample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
class DuAn14Tests {

    @Autowired
    private PropertySourceDemo propertySourceDemo;

    @Autowired
    private Environment environment;

    @Test
    void contextLoads() {
        assertNotNull(propertySourceDemo);
        assertNotNull(environment);
    }

    @Test
    void testValueAnnotationPropertiesInjected() {
        assertEquals("com.mysql.jdbc.Driver", propertySourceDemo.getDriver());
        assertEquals("jdbc:mysql://localhost:3306/dev_db", propertySourceDemo.getUrl());
        assertEquals("root", propertySourceDemo.getUsername());
        assertEquals("root", propertySourceDemo.getPassword());
    }

    @Test
    void testEnvironmentPropertiesLoaded() {
        assertEquals("com.mysql.jdbc.Driver", environment.getProperty("jdbc.driver"));
        assertEquals("jdbc:mysql://localhost:3306/dev_db", environment.getProperty("jdbc.url"));
        assertEquals("root", environment.getProperty("jdbc.username"));
        assertEquals("root", environment.getProperty("jdbc.password"));
    }
}
