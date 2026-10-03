package net.javaguides.spring.scope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class DuAn13Tests {

    @Test
    void testSingletonScopeBehavior() {
        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            MessageService messageService1 = context.getBean(MessageService.class);
            messageService1.setMessage("TwitterMessageService Implementation");
            assertEquals("TwitterMessageService Implementation", messageService1.getMessage());

            MessageService messageService2 = context.getBean(MessageService.class);
            // Đối tượng thứ hai phải là cùng một thể hiện (Singleton)
            assertSame(messageService1, messageService2);
            assertEquals(messageService1.hashCode(), messageService2.hashCode());
            // Trạng thái được chia sẻ: messageService2 nhìn thấy thay đổi do messageService1 thực hiện
            assertEquals("TwitterMessageService Implementation", messageService2.getMessage());
        }
    }
}
