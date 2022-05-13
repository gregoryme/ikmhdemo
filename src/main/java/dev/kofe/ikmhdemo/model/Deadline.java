package dev.kofe.ikmhdemo.model;

import lombok.Data;
import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Data
@Entity
public class Deadline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private java.sql.Date deadlineDate = new Date(System.currentTimeMillis());

    private java.sql.Time deadlineTime = new Time(0);

    private String notes;

    @Transient
    private boolean deadlineMissed = false;

}
