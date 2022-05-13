package dev.kofe.ikmhdemo.service;

import dev.kofe.ikmhdemo.model.Application;
import dev.kofe.ikmhdemo.model.Faculty;
import dev.kofe.ikmhdemo.model.Vote;
import dev.kofe.ikmhdemo.repo.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    public List<Vote> getAllVotesByFaculty (Faculty faculty) {
        return voteRepository.findByFaculty(faculty);
    }

    public Vote getVoteByFacultyAndApplication (Faculty faculty, Application application) {
        return voteRepository.findByFacultyAndApplication(faculty, application);
    }

    public Vote saveVote (Vote vote) {
        return voteRepository.save(vote);
    }

    public List<Vote> getAllVotesForTheApplication (Application application) {
        return voteRepository.findByApplication(application);
    }

    public void deleteByApplication (Application application) {
        voteRepository.deleteAllByApplication(application);
    }

    public void deleteByFacultyMember (Faculty faculty) {
        voteRepository.deleteAllByFaculty(faculty);
    }

}
