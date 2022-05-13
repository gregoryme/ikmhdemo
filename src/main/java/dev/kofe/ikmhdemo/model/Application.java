package dev.kofe.ikmhdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String studentName;

    @Column
    private String studentMiddleName;

    @Column
    private String studentSurname;

    @Column
    private int studentAge;           //@ToDo validation

    @Column
    private java.sql.Date d_o_b;       // birthdate

    @Transient
    private String studentEmail;

    @Column
    private String workExampleLink1;

    @Column
    private String workExampleLink2;

    @Column
    private String workExampleLink3;

    @Column(length = 5000)
    @Basic(fetch = FetchType.LAZY)
    @Lob()
    private String motivation;

    @Column(length = 5000)
    @Basic(fetch = FetchType.LAZY)
    @Lob()
    private String bio;

    @Column(length = 1000)
    @Basic(fetch = FetchType.LAZY)
    @Lob()
    private String expectations;

    @Column
    private String specialization;

    @Column(length = 5000)
    @Basic(fetch = FetchType.LAZY)
    @Lob()
    private String notes;

    @Column
    private boolean agreementPrivateDataProcessing = false;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne (fetch = FetchType.LAZY)
    private SchoolStudio schoolStudio;

    @Transient
    private long schoolId;

    @OneToOne
    private User user;

    @OneToMany
    private Set<Vote> votes;

    @Column
    private boolean deadlineMissed = true;

    @Transient
    private int voteValueByTheFaculty;

    @Transient
    private boolean votedByTheFaculty;

    @Transient
    private String messageText;

    @Transient
    private boolean votedAll;

    @Transient
    private float averageVote;

}
