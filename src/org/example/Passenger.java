package org.example;

public class Passenger extends Person {
    private String passportNo, nationality;

    public Passenger(int id, String name, String phone, String email,
                     String passportNo, String nationality) {
        super(id, name, phone, email);
        this.passportNo = passportNo; this.nationality = nationality;
    }
    public String getPassportNo() { return passportNo; }
    public String getNationality() { return nationality; }
}
