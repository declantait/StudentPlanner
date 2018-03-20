package models;

import io.ebean.Model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
/**
 * Entity managed by Ebean
 */
@MappedSuperclass
public class TaskBase extends Model {

    @Id
    public Long id;

}
