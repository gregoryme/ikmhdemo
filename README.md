# IKMH Student Application Management System

The system is developed for International Kids Media House (Vienna, Austria).
The current version of the system is a demo version for testing and check the features.

Demo on Heroku: https://ikmh.kofedev.com

## Base conceptions

<p>The entity USER is on the top of the hierarchy.</p>
<p>User's <b>Email</b> is used as a <i>login</i> and as a <i>username</i> in the table "users".</p>

```mermaid
graph LR;
    USER-->ADMIN;
    USER-->APPLICANT;
    USER-->FACULTY_MEMBER;   
```

<b><i>Applicant</i></b> is a person (senior school student, 14-17 y.o.) who applies
for the IKMH educational program.

<b><i>Faculty Member</i></b> is a person who reviews and votes for the applications.  

<b><i>Admin</i></b> is a person who controls the system and can retrieve some statistic
information about the application process.

<b><i>School Studio</i></b>: as a rule, every applicants is a student (participant) of some school media studio.

```mermaid
graph TD;
    A[School Studio A] --> B[Applicant 1]
    A --> C[Applicant 2]
    A --> D[Applicant 3]
    A --> J[... ...]
    
    E[School Studio B] --> F[Applicant 4]
    E --> G[Applicant 5]
    E --> H[... ...]
    
    I[...] --> K[...]
    I --> L[...]
```

### A base screnplay for Actor "Applicant (Student)"
```mermaid
graph TD;
    A[Applicant] --> B[Registration]
    B --> C[Confitms email]
    B --> D[Fills/corrects an application form before the deadline]
    D --> E[Marks an agreement on data processing]
    E --> F[Submits the application]
    F --> D

```

### A base screenplay for Actor "Faculty Member"
```mermaid
graph TD;
    A[Faculty Member] --> B[Registration]
    B --> D[Faculty member fills/corrects a form with individual data]
    D --> G[Visits a main page for faculty]
    G --> D
    G --> E[Faculty Member waits for the deadline ends]
    E --> F[Faculty Member votes]
    B --> C[Admin confirms a faculty member's email]
```

### A base screenplay for Actor "Admin"
```mermaid
graph TD;
    A[Admin] --> B[Goes to Administration Center page]
    B --> P[Sends email to all users]
    B --> R[Visits Deadline Editor]
    B --> C[Visits Applications List]
    B --> E[Visits School Studios List]
    B --> K[Visits Faculty Members List]
    K --> L[Visits info page on the faculty member]
    L --> M[Deletes faculty member account]
    L --> N[Sends email to faculty member]
    C --> D[Sorts by surname or votes]
    C --> F[Visits info page on the applicant]
    F --> G[Deletes the application]
    F --> O[Sends email to applicant]
    E --> H[Adds new school studio to the list]
    E --> J[Visits info page on the school studio]
    J --> I[Deletes the school studio]
    R --> S[Corrects the deadline]
```
