package com.vaadin.componentfactory.selectiongrid.bean;

import java.time.LocalDate;

public class Person implements Cloneable {
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private Address address;
    private String phoneNumber;
    private MaritalStatus maritalStatus;
    private LocalDate birthDate;
    private boolean isSubscriber;
    private String email;

    public Person() {

    }

    public Person(int id, String firstName, String lastName, int age,
                  Address address, String phoneNumber) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public Person(int id, String firstName, String lastName, int age,
                  Address address, String phoneNumber,
                  MaritalStatus maritalStatus, LocalDate birthDate) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.maritalStatus = maritalStatus;
        this.birthDate = birthDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getImage() {
        return "https://randomuser.me/api/portraits/men/" + getId()
            + ".jpg";
    }

    public boolean isSubscriber() {
        return isSubscriber;
    }

    public void setSubscriber(boolean isSubscriber) {
        this.isSubscriber = isSubscriber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Person)) {
            return false;
        }
        Person other = (Person) obj;
        return id == other.id;
    }

    @Override
    public String toString() {
        return firstName;
    }

    @Override
    public Person clone() { //NOSONAR
        try {
            return (Person) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(
                "The Person object could not be cloned.", e);
        }
    }
}