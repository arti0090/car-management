package classic_car.cars.Models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class User {
    private @Id
    @GeneratedValue
    Long id;
    private String name;
    private String surname;

    User() {}

    public User(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }
}
