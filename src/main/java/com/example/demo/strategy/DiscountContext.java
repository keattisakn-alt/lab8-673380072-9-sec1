package com.example.demo.strategy;

public class DiscountContext {

    private DiscountStrategy strategy;

    public DiscountContext(DiscountStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculate(double price) {
        return strategy.calculate(price);
    }
}