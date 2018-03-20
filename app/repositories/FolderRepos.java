package repositories;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Folder;
import play.db.ebean.EbeanConfig;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class FolderRepos{

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public FolderRepos(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }


    // java async https://www.playframework.com/documentation/2.6.x/JavaAsync
    public CompletionStage<Map<String, String>> options() {
        return supplyAsync(() -> ebeanServer.find(Folder.class).orderBy("name").findList(), executionContext)
                .thenApply(list -> {
                    HashMap<String, String> options = new LinkedHashMap<String, String>();
                    for (Folder f : list) {
                        options.put(f.id.toString(), f.name);
                    }
                    return options;
                });
    }
}