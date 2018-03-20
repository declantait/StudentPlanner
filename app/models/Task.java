package models;

import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Task extends TaskBase {

    //universal version identifier for class
    private static final long serialVersionUID = 1L;

    @Constraints.Required
    public String name;

    @Formats.DateTime(pattern="MM-dd-yyyy")
    public Date dueDate;

    public boolean important;

    @ManyToOne
    public Folder folder;
}