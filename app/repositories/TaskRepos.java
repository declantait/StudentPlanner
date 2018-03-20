package repositories;

import io.ebean.*;
import models.Folder;
import models.Task;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class TaskRepos{

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public TaskRepos(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    /**
     * Return a paged list of tasks
     *
     * @param page     Page to display
     * @param pageSize Number of tasks per page
     * @param sortBy   Task property used for sorting
     * @param order    Sort order (either or asc or desc)
     * @param filter   Filter applied on the name column
     */

    //task list display
    //https://ebean-orm.github.io/apidoc/10/io/ebean/PagedList.html
    public CompletionStage<PagedList<Task>> page(int page, int pageSize, String sortBy, String order, String filter) {
        return supplyAsync(() ->
                ebeanServer.find(Task.class).where()
                        .ilike("name", "%" + filter + "%")
                        .orderBy(sortBy + " " + order)
                        .fetch("folder")
                        .setFirstRow(page * pageSize)
                        .setMaxRows(pageSize)
                        .findPagedList(), executionContext);
    }

    //task search
    //https://ebean-orm.github.io/apidoc/10/io/ebean/Ebean.html#find-java.lang.Class-
    public CompletionStage<Optional<Task>> lookup(Long id) {
        return supplyAsync(() -> Optional.ofNullable(ebeanServer.find(Task.class).setId(id).findOne()), executionContext);
    }


    //update task
    //http://ebean-orm.github.io/docs/transactions/
    public CompletionStage<Optional<Long>> update(Long id, Task newTaskData) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Optional<Long> value = Optional.empty();
            try {
                Task savedTask = ebeanServer.find(Task.class).setId(id).findOne();
                if (savedTask != null) {
                    savedTask.folder = newTaskData.folder;
                    savedTask.dueDate = newTaskData.dueDate;
                    savedTask.name = newTaskData.name;

                    savedTask.update();
                    txn.commit();
                    value = Optional.of(id);
                }
            } finally {
                txn.end();
            }
            return value;
        }, executionContext);
    }

    //delete task
    public CompletionStage<Optional<Long>>  delete(Long id) {
        return supplyAsync(() -> {
            try {
                final Optional<Task> taskOptional = Optional.ofNullable(ebeanServer.find(Task.class).setId(id).findOne());
                taskOptional.ifPresent(Model::delete);
                return taskOptional.map(t -> t.id);
            } catch (Exception e) {
                return Optional.empty();
            }
        }, executionContext);
    }

    //task add
    public CompletionStage<Long> insert(Task task) {
        return supplyAsync(() -> {
            task.id = System.currentTimeMillis(); // gives unique id using timestamp
            ebeanServer.insert(task);
            return task.id;
        }, executionContext);
    }

    public CompletionStage<Map<String, String>> options() {
        return supplyAsync(() -> ebeanServer.find(Task.class).orderBy("name").findList(), executionContext)
                .thenApply(list -> {
                    HashMap<String, String> options = new LinkedHashMap<String, String>();
                    for (Task t : list) {
                        options.put(t.id.toString(), t.name);
                    }
                    return options;
                });
    }


}