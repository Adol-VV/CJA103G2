package com.momento.emp.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "emp")
public class EmpVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMP_ID")
    private Integer empId;

    @Column(name = "EMP_NAME", length = 50, nullable = false)
    private String empName;

    @Column(name = "JOB_TITLE", length = 50, nullable = false)
    private String jobTitle;

    @Column(name = "ACCOUNT", length = 50, nullable = false, unique = true)
    private String account;

    @Column(name = "PASSWORD", length = 255, nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "STATUS", columnDefinition = "TINYINT DEFAULT 1")
    private Byte status;


    @OneToMany(mappedBy = "emp", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmpAuthorityVO> authorities = new HashSet<>();

    public EmpVO() {
    }

    public EmpVO(Integer empId, String empName, String jobTitle, String account, String password,
                 LocalDateTime createdAt, Byte status) {
        this.empId = empId;
        this.empName = empName;
        this.jobTitle = jobTitle;
        this.account = account;
        this.password = password;
        this.createdAt = createdAt;
        this.status = status;
    }


    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getJobTitle() { return jobTitle; }

    public  void setJobTitle(String jobTitle) {this.jobTitle = jobTitle; }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Set<EmpAuthorityVO> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<EmpAuthorityVO> authorities) {
        this.authorities = authorities;
    }

    public void addAuthority(EmpAuthorityVO authority) {
        authorities.add(authority);
        authority.setEmp(this);
    }

    public void removeAuthority(EmpAuthorityVO authority) {
        authorities.remove(authority);
        authority.setEmp(null);
    }

    @Override
    public String toString() {
        return "Emp{" +
                "empId=" + empId +
                ", empName='" + empName + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", account='" + account + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
