package controllers;

import models.Task;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import repositories.FolderRepos;
import repositories.TaskRepos;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * Manage a database of tasks
 */
public class HomeController extends Controller {

    private final TaskRepos taskRepos;
    private final FolderRepos folderRepos;
    // private final SubTaskRepos subtaskRepos;
    private final FormFactory formFactory;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public HomeController(FormFactory formFactory,
                          TaskRepos taskRepos,
                          FolderRepos folderRepos,
                          HttpExecutionContext httpExecutionContext) {
        this.taskRepos = taskRepos;
        this.formFactory = formFactory;
        this.folderRepos = folderRepos;
        this.httpExecutionContext = httpExecutionContext;
    }

    /**
     * This result directly redirect to application home.
     */
    private Result GO_HOME = Results.redirect(
            routes.HomeController.list(0, "name", "asc", "")
    );

    /**
     * Handle default path requests, redirect to tasks list
     */
    public Result index() {
        return GO_HOME;
    }

    /**
     * Display the paginated list of tasks.
     *
     * @param page   Current page number (starts from 0)
     * @param sortBy Column to be sorted
     * @param order  Sort order (either asc or desc)
     * @param filter Filter applied on task names
     */
    public CompletionStage<Result> list(int page, String sortBy, String order, String filter) {
        // Run a db operation in another thread (using DatabaseExecutionContext)
        return taskRepos.page(page, 10, sortBy, order, filter).thenApplyAsync(list -> {
            // This is the HTTP rendering thread context
            return ok(views.html.list.render(list, sortBy, order, filter));
        }, httpExecutionContext.current());
    }

    /**
     * Display the 'edit form' of an existing task.
     *
     * @param id Id of the task to edit
     */
    public CompletionStage<Result> edit(Long id) {

        // Run a db operation in another thread (using DatabaseExecutionContext)
        CompletionStage<Map<String, String>> foldersFuture = folderRepos.options();

        // Run the lookup also in another thread, then combine the results:
        return taskRepos.lookup(id).thenCombineAsync(foldersFuture, (taskOptional, folders) -> {
            // This is the HTTP rendering thread context
            Task t = taskOptional.get();
            Form<Task> taskForm = formFactory.form(Task.class).fill(t);
            return ok(views.html.editForm.render(id, taskForm, folders));
        }, httpExecutionContext.current());
    }

    /**
     * Handle the 'edit form' submission
     *
     * @param id Id of the task to edit
     */
    public CompletionStage<Result> update(Long id) throws PersistenceException {
        Form<Task> taskForm = formFactory.form(Task.class).bindFromRequest();
        if (taskForm.hasErrors()) {
            // Run folders db operation and then render the failure case
            return folderRepos.options().thenApplyAsync(folders -> {
                // This is the HTTP rendering thread context
                return badRequest(views.html.editForm.render(id, taskForm, folders));
            }, httpExecutionContext.current());
        } else {
            Task newTaskData = taskForm.get();
            // Run update operation and then flash and then redirect
            return taskRepos.update(id, newTaskData).thenApplyAsync(data -> {
                // This is the HTTP rendering thread context
                flash("success", "Task " + newTaskData.name + " has been updated");
                return GO_HOME;
            }, httpExecutionContext.current());
        }
    }

    /**
     * Display the 'new task form'.
     */
    public CompletionStage<Result> create() {
        Form<Task> taskForm = formFactory.form(Task.class);
        // Run folders db operation and then render the form
        return folderRepos.options().thenApplyAsync((Map<String, String> folders) -> {
            // This is the HTTP rendering thread context
            return ok(views.html.createForm.render(taskForm, folders));
        }, httpExecutionContext.current());
    }

    /**
     * Handle the 'new task form' submission
     */
    public CompletionStage<Result> save() {
        Form<Task> taskForm = formFactory.form(Task.class).bindFromRequest();
        if (taskForm.hasErrors()) {
            // Run folders db operation and then render the form
            return folderRepos.options().thenApplyAsync(folders -> {
                // This is the HTTP rendering thread context
                return badRequest(views.html.createForm.render(taskForm, folders));
            }, httpExecutionContext.current());
        }

        Task task = taskForm.get();
        // Run insert db operation, then redirect
        return taskRepos.insert(task).thenApplyAsync(data -> {
            // This is the HTTP rendering thread context
            flash("success", "Task " + task.name + " has been created");
            return GO_HOME;
        }, httpExecutionContext.current());
    }

    /**
     * Handle task deletion
     */
    public CompletionStage<Result> delete(Long id) {
        // Run delete db operation, then redirect
        return taskRepos.delete(id).thenApplyAsync(v -> {
            // This is the HTTP rendering thread context
            flash("success", "Task has been deleted");
            return GO_HOME;
        }, httpExecutionContext.current());
    }
}

