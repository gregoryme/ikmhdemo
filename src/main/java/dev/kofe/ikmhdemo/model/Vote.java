package dev.kofe.ikmhdemo.model;

import javax.persistence.*;

//@Data
@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    private Faculty faculty;

    @ManyToOne (fetch = FetchType.LAZY)
    private Application application;

    @Column
    private int voteValue;

    @Column
    private String notes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public int getVoteValue() {
        return voteValue;
    }

    public void setVoteValue(int voteValue) {
        this.voteValue = voteValue;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
