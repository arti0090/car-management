package classic_car.cars.Controllers;

import classic_car.cars.Models.Enums.Status;
import classic_car.cars.Models.Task;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TaskResourceAssembler implements RepresentationModelAssembler<Task, EntityModel<Task>> {

    @Override
    public EntityModel<Task> toModel(Task task) {

        EntityModel<Task> taskEntityModel = new EntityModel<>(task,
                linkTo(methodOn(TaskController.class).getTaskById(task.getId())).withSelfRel(),
                linkTo(methodOn(TaskController.class).getAllTasks()).withRel("tasks"));

        if (task.getStatus() == Status.IN_PROGRESS) {
            taskEntityModel.add(
                    linkTo(methodOn(TaskController.class)
                    .set_todo(task.getId())).withRel("set_todo"));
            taskEntityModel.add(
                    linkTo(methodOn(TaskController.class)
                    .cancel(task.getId())).withRel("cancel"));
            taskEntityModel.add(
                    linkTo(methodOn(TaskController.class)
                    .complete(task.getId())).withRel("complete"));
        }
        return taskEntityModel;
    }


}
