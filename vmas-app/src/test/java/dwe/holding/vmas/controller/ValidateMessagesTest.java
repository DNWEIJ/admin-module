package dwe.holding.vmas.controller;

import dwe.holding.VmasApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = VmasApplication.class, properties = {
        "spring.messages.fallback-to-system-locale=false",
        "spring.messages.use-code-as-default-message=false"
})
@ActiveProfiles("local")
public class ValidateMessagesTest {

    @Autowired
    private MessageSource messageSource;

    private final List<Locale> supportedLocales = List.of(
            Locale.ENGLISH,
            new Locale("nl")
    );

    private final List<String> basenames = List.of("messages");

    @Test
    void allMessagesMustBeTranslatedStrictly() {
        List<String> errors = new ArrayList<>();
        for (String basename : basenames) {
            Map<Locale, Set<String>> localeKeyMap = new HashMap<>();
            for (Locale locale : supportedLocales) {
                localeKeyMap.put(locale, loadKeys(basename, locale, errors));
            }
            Set<String> allKeys = new HashSet<>();
            localeKeyMap.values().forEach(allKeys::addAll);
            for (String key : allKeys) {
                for (Locale locale : supportedLocales) {
                    Set<String> keys = localeKeyMap.get(locale);
                    if (!keys.contains(key)) {
                        errors.add(String.format("[MISSING] basename=%s key=%s locale=%s", basename, key, locale));
                        continue;
                    }
                    try {
                        String value = messageSource.getMessage(key, null, null, locale);
                        if (value == null || value.isBlank()) {
                            errors.add(String.format("[EMPTY] basename=%s key=%s locale=%s", basename, key, locale));
                        }
                    } catch (Exception e) {
                        errors.add(String.format("[RESOLUTION FAILED] basename=%s key=%s locale=%s", basename, key, locale));
                    }
                }
            }
        }
        if (!errors.isEmpty()) {
            String report = "\n==== I18N VALIDATION FAILED ====\n" + String.join("\n", errors) + "\n================================\n";
            fail(report);
        }
    }

    private Set<String> loadKeys(String basename, Locale locale, List<String> errors) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(basename, locale);
            return bundle.keySet();
        } catch (MissingResourceException e) {
            errors.add(String.format(
                    "[BUNDLE MISSING] basename=%s locale=%s",
                    basename, locale));
            return Collections.emptySet();
        }
    }
}