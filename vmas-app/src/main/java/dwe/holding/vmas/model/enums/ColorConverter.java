package dwe.holding.vmas.model.enums;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ColorConverter implements Converter<String, ColorEnum> {

    @Override
    public ColorEnum convert(String source) {
        return ColorEnum.fromValue(source);
    }
}
