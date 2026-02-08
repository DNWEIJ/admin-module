package dwe.holding.customer.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
public class ZipCodeApi {
    private final RestClient restClient = RestClient.create();

    public Optional<Address> getAddress(String ZipCode, String houseNumber) {
        return
                restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("https")
                                .host("openpostcode.nl")
                                .path("/api/address")
                                .queryParam("postcode", ZipCode)
                                .queryParam("huisnummer", houseNumber)
                                .build()
                        )
                        .exchange((req, res) -> {
                            if (res.getStatusCode() == HttpStatus.OK) {
                                return Optional.of(res.bodyTo(Address.class));
                            }
                            if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                                @Nullable AddressErrors addressErrors = res.bodyTo(AddressErrors.class);
                                return Optional.of(
                                        new Address(null,
                                                addressErrors.suggestions == null ? "" : String.join(", ", addressErrors.suggestions),
                                                null, null)
                                );
                            }
                            return Optional.empty();
                        });
    }

    public Optional<Address> getAddressViaStreetAndCity(String streetName, String houseNumber,String city) {
        return
                restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("https")
                                .host("openpostcode.nl")
                                .path("/api/postcode")
                                .queryParam("straat", streetName)
                                .queryParam("huisnummer", houseNumber)
                                .queryParam("plaats", city)
                                .build()
                        )
                        .exchange((req, res) -> {
                            if (res.getStatusCode() == HttpStatus.OK) {
                                return Optional.of(res.bodyTo(Address.class));
                            }
                            if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                                AddressErrors addressErrors = res.bodyTo(AddressErrors.class);
                                return Optional.of(
                                        new Address(null,
                                                addressErrors.suggestions == null ? "" : String.join(", ", filterHouseNumber(addressErrors.suggestions, houseNumber))
                                                                ,null, null)
                                );
                            }
                            return Optional.empty();
                        });
    }

    public record Address(@JsonProperty("straat") String street,
                          @JsonProperty("huisnummer") String houseNumber,
                          @JsonProperty("postcode") String zipCode,
                          @JsonProperty("woonplaats") String city) {


        @Override
        public String toString() {
            return "%s %s, %s %s".formatted(street, houseNumber, zipCode, city);
        }
    }

    public record AddressErrors(String error,
                                List<String> suggestions
    ) {
    }

    public record AddressZipCodes(List<String> postcodes
    ) {
    }

    private List<String> filterHouseNumber(List<String> suggestions, String houseNumber) {
        List<String> result = suggestions.stream().filter(suggestion -> suggestion.startsWith(houseNumber)).toList();
        return result.isEmpty() ? suggestions : result;
    }
}