package classic_car.cars.Global;

import classic_car.cars.Models.Enums.Status;
import classic_car.cars.Models.Task;
import classic_car.cars.Models.User;
import classic_car.cars.Repositories.TaskRepository;
import classic_car.cars.Repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, TaskRepository taskRepository) {
        return args -> {
            log.info("Preloading " + userRepository.save(new User("Bilbo Baggins", "burglar")));
            log.info("Preloading " + userRepository.save(new User("Frodo Baggins", "thief")));
            log.info("Preloading " + taskRepository.save(new Task("Check wheels", Status.TO_DO)));
            log.info("Preloading " + taskRepository.save(new Task("Check bearings", Status.IN_PROGRESS)));
        };
    }
}
