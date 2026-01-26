package com.momento.notify.model;

import com.momento.emp.model.EmpVO;
import com.momento.member.model.MemberVO;
import com.momento.organizer.model.OrganizerVO;
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
    private Integer systemNotifyId;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private MemberVO memberVO;

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
    private Integer notifyStatus;

}
