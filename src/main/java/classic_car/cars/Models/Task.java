package classic_car.cars.Models;

import classic_car.cars.Models.Enums.Status;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
//Use @table when change name (f.e. order is special name and cannot be used)
//@Table(name = "USER_JOB")
public class Task {
    private @Id
    @GeneratedValue
    Long id;

    private String description;
    private Status status;

    Task(){}

    public Task(String description, Status status) {
        this.description = description;
        this.status = status;
    }
}
