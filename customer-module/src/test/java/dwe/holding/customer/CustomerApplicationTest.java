package dwe.holding.customer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomerApplicationTest {

    @Test
    void generateSchema() {
        // The schema will be generated automatically at context startup.
        // No code needed — starting the context is enough.
        System.out.println("✅ Schema generation complete. Check for 'hsqldb-schema.sql'");
    }

}