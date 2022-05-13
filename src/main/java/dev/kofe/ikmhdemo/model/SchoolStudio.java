package dev.kofe.ikmhdemo.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
//@Data
public class SchoolStudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String schoolName;

    private String schoolCity;

    private String contactPersonName;

    private String contactEmail;

    private String contactPhone;

    private String notes;

    @OneToMany
    private Collection<Application> applications = new ArrayList<>();


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolCity() {
        return schoolCity;
    }

    public void setSchoolCity(String schoolCity) {
        this.schoolCity = schoolCity;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Collection<Application> getApplications() {
        return applications;
    }

    public void setApplications(Collection<Application> applications) {
        this.applications = applications;
    }
}
