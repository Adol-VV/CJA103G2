package com.momento.notify.model;

import com.momento.emp.model.EmpVO;
import com.momento.organizer.model.OrganizerVO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "ORG_NOTIFY")
public class OrganizerNotifyVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L ; //作為版本確認，確保資料穩定性

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORG_NOTIFY_ID")
    private Integer organizerNotifyId;

    @ManyToOne
    @JoinColumn(name = "ORGANIZER_ID")
    private OrganizerVO organizerVO;

    @ManyToOne
    @JoinColumn(name = "EMP_ID")
    private EmpVO empVO;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "IS_READ", insertable = false)
    private Integer isRead;

    @Column(name = "CREATED_AT", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Transient
    @Getter
    @Setter
    private Integer notifyStatus;

    @Transient
    @Getter
    @Setter
    private  String targetId;

}