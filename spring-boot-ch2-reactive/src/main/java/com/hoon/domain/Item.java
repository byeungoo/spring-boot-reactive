package com.hoon.domain;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.awt.*;
import java.util.Date;

@Getter @Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Item {

    private @Id String id;
    private String name;
    private double price;
    private String description;

    private String distributorRegion;
    private Date releaseDate;
    private int availableUnits;
    private Point location;
    private boolean active;

    private Item() {}

    public Item(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

}
