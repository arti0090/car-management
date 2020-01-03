package classic_car.cars.Controllers;

import classic_car.cars.Exceptions.TaskNotFoundException;
import classic_car.cars.Models.Enums.Status;
import classic_car.cars.Models.Task;
import classic_car.cars.Repositories.TaskRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.vnderrors.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TaskController {

    private final TaskRepository repository;
    private final TaskResourceAssembler assembler;

    TaskController(TaskRepository repository, TaskResourceAssembler assembler){
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/tasks")
    CollectionModel<EntityModel<Task>> getAllTasks() {
        List<EntityModel<Task>> tasks = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return new CollectionModel<>(tasks,
                linkTo(methodOn(TaskController.class).getAllTasks()).withSelfRel());
    }

    @GetMapping("/tasks/{id}")
    EntityModel<Task> getTaskById(@PathVariable Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return assembler.toModel(task);
    }

    @PutMapping("/tasks/{id}")
    ResponseEntity<?> UpdateTask(@RequestBody Task updateTask, @PathVariable Long id) throws URISyntaxException {
        Task updatedTask = repository.findById(id)
                .map(task -> {
                    task.setDescription(updateTask.getDescription());
                    task.setStatus(updateTask.getStatus());
                    return repository.save(task);
                })
                .orElseGet(() -> {
                    updateTask.setId(id);
                    return repository.save(updateTask);
                });

        EntityModel<Task> entityModel = assembler.toModel(updatedTask);

        return ResponseEntity
                .created(new URI(entityModel.getRequiredLink("self").getHref()))
                .body(entityModel);
    }

    @DeleteMapping("/tasks/{id}")
    ResponseEntity<?> deleteTask(@PathVariable Long id){
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/tasks/{id}/set_todo")
    ResponseEntity<RepresentationModel> set_todo(@PathVariable Long id){
        Task task = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

        if (task.getStatus() == Status.IN_PROGRESS) {
            task.setStatus(Status.TO_DO);
            return ResponseEntity.ok(assembler.toModel(repository.save(task)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError("Method not allowed", "Cannot change status " + task.getStatus() + " to " + Status.TO_DO));
    }

    @PutMapping("/tasks/{id}/cancel")
    ResponseEntity<RepresentationModel> cancel(@PathVariable Long id){
        Task task = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

        if (task.getStatus() == Status.IN_PROGRESS) {
            task.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(assembler.toModel(repository.save(task)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError("Method not allowed", "Cannot change status " + task.getStatus() + " to " + Status.CANCELLED));
    }

    @PutMapping("/tasks/{id}/complete")
    ResponseEntity<RepresentationModel> complete(@PathVariable Long id){
        Task task = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

        if (task.getStatus() == Status.IN_PROGRESS) {
            task.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(assembler.toModel(repository.save(task)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError("Method not allowed", "Cannot change status " + task.getStatus() + " to " + Status.COMPLETED));
    }
}
