package com.boyitong.controller;

import com.boyitong.common.Result;
import com.boyitong.entity.Task;
import com.boyitong.repository.TaskRepository;
import com.boyitong.service.AuditLogService;
import com.boyitong.service.UserResolver;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final AuditLogService auditLogService;
    private final UserResolver userResolver;

    public TaskController(TaskRepository taskRepository, AuditLogService auditLogService, UserResolver userResolver) {
        this.taskRepository = taskRepository;
        this.auditLogService = auditLogService;
        this.userResolver = userResolver;
    }

    @GetMapping
    public Result<List<Task>> getMyTasks(Authentication auth) {
        return Result.success(taskRepository.findByAssigneeOrderByCreatedAtDesc(auth.getName()));
    }

    @GetMapping("/pending")
    public Result<List<Task>> getPending(Authentication auth) {
        return Result.success(taskRepository.findByAssigneeAndCompletedFalseOrderByDueDateAsc(auth.getName()));
    }

    @GetMapping("/pending-count")
    public Result<Long> getPendingCount(Authentication auth) {
        return Result.success(taskRepository.countByAssigneeAndCompletedFalse(auth.getName()));
    }

    @PostMapping
    public Result<Task> create(@RequestBody Task task, Authentication auth) {
        task.setId(null);
        task.setAssignee(auth.getName());
        task.setAssigneeUserId(userResolver.getUserId(auth.getName()));
        Task saved = taskRepository.save(task);
        auditLogService.log(auth.getName(), "CREATE_TASK", "Task", String.valueOf(saved.getId()),
                "创建任务: " + task.getTitle());
        return Result.success(saved);
    }

    @PostMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id, Authentication auth) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        task.setCompleted(true);
        task.setCompletedAt(LocalDateTime.now());
        taskRepository.save(task);
        auditLogService.log(auth.getName(), "COMPLETE_TASK", "Task", String.valueOf(id),
                "完成任务: " + task.getTitle());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return Result.success();
    }
}