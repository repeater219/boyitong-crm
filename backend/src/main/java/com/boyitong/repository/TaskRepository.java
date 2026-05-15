package com.boyitong.repository;

import com.boyitong.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssigneeOrderByCreatedAtDesc(String assignee);
    List<Task> findByAssigneeAndCompletedFalseOrderByDueDateAsc(String assignee);
    long countByAssigneeAndCompletedFalse(String assignee);
}