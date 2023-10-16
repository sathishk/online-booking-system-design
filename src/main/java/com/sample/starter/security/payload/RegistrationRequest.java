package com.sample.starter.security.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

/**
 * The type Registration request.
 */
public class RegistrationRequest {


    /**
     * firstName.
     */
    @NotBlank
    private String firstName;

    /**
     * Date of Birth.
     */
    @Past
    private LocalDate dob;

    /**
     * imageUrl.
     */
    @NotBlank
    private String lastName;

    /**
     * getPassword.
     *
     * @return password
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * setPassword.
     *
     * @param thepassword
     */
    public void setFirstName(final String thepassword) {
        this.firstName = thepassword;
    }

    /**
     * get Image Url.
     *
     * @return imageUrl
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets Image Url.
     *
     * @param aimageUrl
     */
    public void setLastName(final String aimageUrl) {
        this.lastName = aimageUrl;
    }


    /**
     * Gets Dob.
     * @return dob
     */
    public LocalDate getDob() {
        return dob;
    }

    /**
     * sets Dob.
     * @param aDob
     */
    public void setDob(final LocalDate aDob) {
        this.dob = aDob;
    }
}
