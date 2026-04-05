package com.serenitydojo.playwright.api;

import net.datafaker.Faker;

public record User(
        String first_name,
        String last_name,
        Address address,
        String phone,
        String dob,
        String email,
        String password) {

    record Address(
            String street,
            String city,
            String state,
            String country,
            String postal_code
    ) {

        public static Address getAddress(){

            Faker fake = new Faker();

            return new Address(
                    fake.address().streetAddress(),
                    fake.address().city(),
                    fake.address().state(),
                    fake.country().name(),
                    fake.address().postcode()
            );
        }
    }

    public static User randomUser() {

        Faker fake = new Faker();

        return new User(
                fake.name().firstName(),
                fake.name().lastName(),
                Address.getAddress(),
                fake.phoneNumber().phoneNumber(),
                fake.timeAndDate().birthday("yyyy-MM-dd"),
                fake.internet().emailAddress(),
                "Q1@w2E3r");
    }

    public Object withPassword(String password) {

        return new User(
                this.first_name,
                this.last_name,
                this.address,
                this.phone,
                this.dob,
                this.email,
                password
        );
    }
}
