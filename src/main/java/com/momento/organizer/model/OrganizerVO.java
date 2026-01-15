package com.momento.organizer.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ORGANIZER")
public class OrganizerVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORGANIZER_ID")
    private Integer organizerId;

    @Column(name = "ACCOUNT", length = 50, nullable = false, unique = true)
    private String account;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    @Column(name = "ADDRESS", length = 200)
    private String address;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Lob
    @Column(name = "INTRODUCTION", columnDefinition = "LONGTEXT")
    private String introduction;

    @Column(name = "STATUS", columnDefinition = "TINYINT DEFAULT 0")
    private Byte status;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    public OrganizerVO(){
    }

    public OrganizerVO(Integer organizerId, String account, String password, String name,
                       String address, String phone, String introduction, Byte status,
                       LocalDateTime createdAt) {
        this.organizerId = organizerId;
        this.account = account;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.introduction = introduction;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Integer organizerId) {
        this.organizerId = organizerId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString(){
        return "OrganizerVO{" +
                "organizerId=" + organizerId +
                ", account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
