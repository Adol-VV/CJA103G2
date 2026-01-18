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

    @Column(name = "OWNER_NAME", length = 50, nullable = false)
    private String ownerName;

    @Column(name = "PHONE", length = 20)
    private String phone;

    @Column(name = "EMAIL",  length = 100, nullable = false, unique = true)
    private String email;

    @Lob
    @Column(name = "INTRODUCTION", columnDefinition = "LONGTEXT")
    private String introduction;

    @Column(name = "BANK_CODE", length = 10, nullable = false)
    private String bankCode;

    @Column(name = "BANK_ACCOUNT", length = 20, nullable = false)
    private String bankAccount;

    @Column(name = "ACCOUNT_NAME", length = 100, nullable = false)
    private String accountName;

    @Column(name = "STATUS", columnDefinition = "TINYINT DEFAULT 0")
    private Byte status;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    public OrganizerVO(){
    }

    public OrganizerVO(Integer organizerId, String account, String password, String name,
                       String ownerName, String phone, String email, String introduction,
                       String bankCode, String bankAccount, String accountName,
                       Byte status, LocalDateTime createdAt) {
        this.account = account;
        this.password = password;
        this.name = name;
        this.ownerName = ownerName;
        this.phone = phone;
        this.email = email;
        this.introduction = introduction;
        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
        this.accountName = accountName;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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
                ", ownerName='" + ownerName + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
