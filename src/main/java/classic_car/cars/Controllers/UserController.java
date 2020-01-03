package classic_car.cars.Controllers;

import classic_car.cars.Exceptions.UserNotFoundException;
import classic_car.cars.Models.User;
import classic_car.cars.Repositories.UserRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserController {

    private final UserRepository repository;
    private final UserResourceAssembler assembler;

    UserController(UserRepository repository, UserResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    //Aggregate root

    @GetMapping("/users")
    CollectionModel<EntityModel<User>> getAllUsers(){
       List<EntityModel<User>> users = repository.findAll().stream()
               .map(assembler::toModel)
               .collect(Collectors.toList());

       return new CollectionModel<>(users,
               linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }

    @PostMapping("/users")
    ResponseEntity<?> createUser(@RequestBody User newUser) throws URISyntaxException {

        EntityModel<User> entityModel = assembler.toModel(repository.save(newUser));

        return ResponseEntity
                .created(new URI(entityModel.getRequiredLink("self").getHref()))
                .body(entityModel);
    }

    //Single item
    @GetMapping("/users/{id}")
    EntityModel<User> getUserById(@PathVariable Long id){
        User user =  repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return assembler.toModel(user);
    }

    @PutMapping("/users/{id}")
    ResponseEntity<?> updateUser(@RequestBody User updateUser, @PathVariable Long id) throws URISyntaxException {
        User updatedUser = repository.findById(id)
                .map(user -> {
                    user.setName(updateUser.getName());
                    user.setSurname(updateUser.getSurname());
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    updateUser.setId(id);
                    return repository.save(updateUser);
                });

        EntityModel<User> entityModel = assembler.toModel(updatedUser);

        return ResponseEntity
                .created(new URI(entityModel.getRequiredLink("self").getHref()))
                .body(entityModel);
    }

    @DeleteMapping("/users/{id}")
    ResponseEntity<?> deleteUser(@PathVariable Long id){

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
