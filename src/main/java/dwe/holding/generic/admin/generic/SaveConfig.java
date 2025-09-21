package dwe.holding.generic.admin.generic;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SaveConfig {
    String successMessage();

    String errorView();

    String redirectBase();
}