package inventory.src.test.java.sigebi.inventory;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
@Disabled
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "equipment-service.url=http://localhost:8089"
})
class InventoryApplicationTests {

    @Test
    void contextLoads() {
    }
}
