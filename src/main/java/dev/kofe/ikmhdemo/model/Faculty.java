package dev.kofe.ikmhdemo.model;

import lombok.Data;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title = null;

    @Column
    private String name;

    @Column
    private String surname;

    @Column
    private String phone;

    @Column
    private String notes;

    @Transient
    private String email;

    @OneToOne
    private User user;

    @OneToMany
    private Set<Vote> votes;

    @Column
    private boolean active = true;

    @Transient
    private boolean fullVoted = false;

}
