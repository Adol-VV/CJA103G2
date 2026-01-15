package com.momento.notify.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name ="SYS_NOTIFY")
public class SystemNotifyVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SYS_NOTIFY_ID")
    private Integer sysNotifyId;

    @Column(name = "MEMBER_ID")
    private Integer memberId;

    @Column(name = "EMP_ID")
    private Integer empId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "IS_READ", insertable = false)
    private Integer isRead;

    @Column(name = "CREATED_AT", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
