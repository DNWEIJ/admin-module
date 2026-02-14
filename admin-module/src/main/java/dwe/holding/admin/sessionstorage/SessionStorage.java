package dwe.holding.admin.sessionstorage;

import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;


// registered in webconfig
@Component
public class SessionStorage {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> Optional<T> getModule(String moduleName, Class<T> clazz) {
        try {
            JsonNode node = AutorisationUtils.getTempGenericStorage().get(moduleName);
            if (node == null || node.isNull()) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.treeToValue(node, clazz));
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    public <T> void updateModule(String moduleName, T newSettings) {
        try {
            AutorisationUtils.getTempGenericStorage().put(moduleName, objectMapper.valueToTree(newSettings));
        } catch (Exception e) {
            // do nothing
        }
    }
}