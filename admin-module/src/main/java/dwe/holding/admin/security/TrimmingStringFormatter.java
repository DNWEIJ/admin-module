package dwe.holding.admin.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.Formatter;

import java.util.Locale;


@Slf4j
public class TrimmingStringFormatter implements Formatter<String> {

    // for overwriting on specific fields:
//    @Controller
//    public class MyController {
//
//        @InitBinder
//        public void initBinder(WebDataBinder binder) {
//            binder.addCustomFormatter(new Formatter<String>() {
//                @Override
//                public String parse(String text, Locale locale) {
//                    return text; // no trim
//                }
//
//                @Override
//                public String print(String object, Locale locale) {
//                    return object;
//                }
//            }, "specialField");
//        }
//    }
//
//
    public TrimmingStringFormatter() {
        log.info("TrimmingStringFormatter initialized:: for specific fields overwrite via ");
    }

    @Override
    public String parse(String text, Locale locale) {
        if (text == null) return null;
        return text.trim();
        // todo validate if we need to remove \n \r
    }

    @Override
    public String print(String object, Locale locale) {
        return object;
    }
}