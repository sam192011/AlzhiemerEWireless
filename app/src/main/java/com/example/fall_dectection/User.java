package com.example.fall_dectection;

public class User {

    public String email;
    public int steps;
    public double stride;
    public String address;
    public int weight;
    public String phone;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, int steps, double stride, String address, int weight, String phone) {
        this.email = email;
        this.steps = steps;
        this.stride = stride;
        this.address = address;
        this.weight = weight;
        this.phone = phone;
    }

}
