package dev.kofe.ikmhdemo.service;

import dev.kofe.ikmhdemo.model.Deadline;
import dev.kofe.ikmhdemo.repo.DeadlineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeadlineService {

    @Autowired
    private DeadlineRepository deadlineRepository;

    public Deadline getDeadlineById(long id) {
        Optional<Deadline> optionalDeadline = deadlineRepository.findById(id);
        Deadline deadline;
        if (optionalDeadline.isPresent()) {
            deadline = optionalDeadline.get();
        } else {
            deadline = null;
        }
        return deadline;
    }

    public Deadline saveDeadline(Deadline deadline) {
        return deadlineRepository.save(deadline);
    }

    public long countAll () {
        return  deadlineRepository.count();
    }

}
