package net.javaguides.spring.scope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class DuAn12Tests {

    @Test
    void testPrototypeScopeBehavior() {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            MessageService messageService1 = context.getBean(MessageService.class);
            messageService1.setMessage("TwitterMessageService Implementation");
            assertEquals("TwitterMessageService Implementation", messageService1.getMessage());

            MessageService messageService2 = context.getBean(MessageService.class);
            // Đối tượng thứ hai phải là một thể hiện mới hoàn toàn (Prototype)
            assertNotSame(messageService1, messageService2);
            assertNotEquals(messageService1.hashCode(), messageService2.hashCode());
            // Trạng thái message của đối tượng mới phải là null (chưa được set)
            assertNull(messageService2.getMessage());
        }
    }
}
