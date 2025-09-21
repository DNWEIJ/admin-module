package dwe.holding.generic.admin.model.base;

import dwe.holding.generic.admin.exception.SystemException;
import jakarta.persistence.MappedSuperclass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@MappedSuperclass
public class ToString {

    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
        sb.append("[");

        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                try {
                    String fieldName = field.getName();
                    String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method m = getClass().getDeclaredMethod(methodName, (Class[]) null);
                    m.setAccessible(true);
                    Object o = m.invoke(this, (Object[]) null);
                    String fieldValue = (o == null ? "null" : o.toString());

                    sb.append(fieldName).append("=").append(fieldValue).append(",");
                } catch (Exception e) {
                    throw new SystemException("SYS-00001", new Object[]{"BaseBO.toString",
                            "Failure to build toString() by reflection."}, e);
                }
            }
        }

        if (sb.lastIndexOf(",") > 0) {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        sb.append("]");
        return sb.toString();
    }
}