package dwe.holding.admin.tenant;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantEntityListener {

    private static final Map<Class<?>, List<Field>> TENANT_FIELDS_CACHE = new ConcurrentHashMap<>();

    @PrePersist
    @PreUpdate
    public void setTenant(Object entity) {

        if (entity.getClass().getName().equals("dwe.holding.admin.model.tenant.User") && AutorisationUtils.isLoggedIn() == false) return;

        if (entity == null) return;

        List<Field> tenantFields = TENANT_FIELDS_CACHE.computeIfAbsent(
                entity.getClass(),
                this::findTenantFields
        );

        tenantFields.stream()
                .filter(Objects::nonNull)
                .forEach(field -> {
                    ReflectionUtils.makeAccessible(field);
                    Object current = ReflectionUtils.getField(field, entity);
                    if (current == null) {
                        // TODO remove the current check for null; always set it, but for now we need to survice the setup
                        ReflectionUtils.setField(field, entity, AutorisationUtils.getCurrentUserMid());
                    }
                });
    }

    private List<Field> findTenantFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        ReflectionUtils.doWithFields(clazz, field -> {
            if (field.isAnnotationPresent(TenantDiscriminator.class)) {
                result.add(field);
            }
        });
        return result;
    }
}