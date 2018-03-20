package models;

import play.data.validation.Constraints;

import javax.persistence.Entity;

/**
 * Entity managed by Ebean
 */
@Entity
public class Folder extends TaskBase {

    private static final long serialVersionUID = 1L;

    @Constraints.Required
    public String name;

}
