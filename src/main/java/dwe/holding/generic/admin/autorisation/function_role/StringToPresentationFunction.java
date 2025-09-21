package dwe.holding.generic.admin.autorisation.function_role;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Component
public class StringToPresentationFunction implements Converter<String, PresentationFunction> {

    @Override
    public PresentationFunction convert(String source) {
        PresentationFunction pf = new PresentationFunction();
        // PresentationFunction[id=2,name=role_READ,connected=false]
        // haal de inhoud tussen [ ] eruit
        String content = source.substring(source.indexOf('[') + 1, source.indexOf(']'));
        String[] parts = content.split(",");

        for (String part : parts) {
            String[] kv = part.split("=");
            String key = kv[0].trim();
            String value = kv[1].trim();

            switch (key) {
                case "id":
                    pf.setId(Long.parseLong(value));
                    break;
                case "name":
                    pf.name = value;
                    break;
                case "connected":
                    pf.connected = Boolean.parseBoolean(value);
                    break;
            }
        }
        return pf;
    }
}