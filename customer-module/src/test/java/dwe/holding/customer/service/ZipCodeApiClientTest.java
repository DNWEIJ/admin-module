package dwe.holding.customer.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ZipCodeApiClientTest {

    private final ZipCodeApiClient api = new ZipCodeApiClient();

    @Test
    void callsRealOpenPostcodeApi_Succes() {
        ZipCodeApiClient.Address address =
                api.getAddress("2215MT", "18").get();

        assertThat(address).isNotNull();
        assertThat(address.street()).isNotBlank();
        assertThat(address.city()).isNotBlank();
        assertThat(address.zipCode()).isEqualTo("2215MT");
    }

    @Test
    void callsRealOpenPostcodeApi_NotFound() {
        ZipCodeApiClient.Address address = api.getAddress("2215MT", "1800").get();
        assertThat(address).isNotNull();
        assertThat(address.street()).isNull();
        assertThat(address.city()).isNull();
        assertThat(address.houseNumber()).isNotEmpty();
    }

    @Test
    void callsRealOpenPostcodeStreetApi_Succes() {
        ZipCodeApiClient.Address address =
                api.getAddressViaStreetAndCity("Jacob van Lennepkade","2-1", "Amsterdam").get();
        assertThat(address).isNotNull();
        assertThat(address.street()).isNotBlank();
        assertThat(address.city()).isNotBlank();
        assertThat(address.zipCode()).isEqualTo("1053MJ");
    }

}