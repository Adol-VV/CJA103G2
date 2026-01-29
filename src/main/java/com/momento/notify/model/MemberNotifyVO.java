package com.momento.notify.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.momento.member.model.MemberVO;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "MEMBER_NOTIFY") // 請根據你的資料庫實際表名修改
@Data
public class MemberNotifyVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_NOTIFY_ID")
    private Integer memberNotifyId;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    @JsonIgnore
    private MemberVO memberVO;

    @ManyToOne
    @JoinColumn(name = "ORG_NOTIFY_ID")
    @JsonIgnore
    private OrganizerNotifyVO organizerNotifyVO;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "IS_READ")
    private Integer isRead; // 0:未讀, 1:已讀

    @Column(name = "CREATED_AT", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}