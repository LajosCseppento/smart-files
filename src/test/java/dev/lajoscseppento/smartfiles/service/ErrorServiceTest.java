package dev.lajoscseppento.smartfiles.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "dev.lajoscseppento.smartfiles.errorHistorySize=2")
class ErrorServiceTest {

    @Autowired
    private ErrorService errorService;

    @Test
    void test() {
        // TODO assertions
        errorService.addError("Test","S", "E1");
        errorService.addError("Test","S", "E2");

        errorService.getErrors().subscribe(d -> System.out.println("Subscriber 1 to Hot Source: " + d));
        errorService.addError("Test","S", "E3");

        errorService.getErrors().subscribe(d -> System.out.println("Subscriber 2 to Hot Source: " + d));
        errorService.addError("Test","S", "E4");

        errorService.getErrors().subscribe(d -> System.out.println("Subscriber 3 to Hot Source: " + d));
    }

}
