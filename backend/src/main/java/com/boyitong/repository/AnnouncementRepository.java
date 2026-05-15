package com.boyitong.repository;
import com.boyitong.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findAllByOrderByPinnedDescCreatedAtDesc();
}