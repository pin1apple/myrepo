
package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String sessionName;

    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Column(name = "recording_path", length = 500)
    private String recordingPath;

    @Column(name = "transcription_text", columnDefinition = "TEXT")
    private String transcriptionText;

    @Column(name = "session_date")
    private LocalDateTime sessionDate;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }
}