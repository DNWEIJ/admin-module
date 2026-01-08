package dwe.holding.customer.service;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class ZipCodeApiTest {

    private final ZipCodeApi api = new ZipCodeApi(
            new ObjectMapper()
    );

    @Test
    void callsRealOpenPostcodeApi_Succes() {
        ZipCodeApi.Address address =
                api.getAddress("2215MT", "18").get();

        assertThat(address).isNotNull();
        assertThat(address.street()).isNotBlank();
        assertThat(address.city()).isNotBlank();
        assertThat(address.zipCode()).isEqualTo("2215MT");
    }

    @Test
    void callsRealOpenPostcodeApi_NotFound() {
        assertThat(api.getAddress("2215MT", "1800").isEmpty()).isTrue();
    }

    @Test
    void callsRealOpenPostcodeStreetApi_Succes() {
        ZipCodeApi.Address address =
                api.getAddressViaStreet("Jacob van Lennepkade", "Amsterdam").get();
        assertThat(address).isNotNull();
        assertThat(address.street()).isNotBlank();
        assertThat(address.city()).isNotBlank();
        assertThat(address.zipCode()).isEqualTo("2215MT");
    }

}