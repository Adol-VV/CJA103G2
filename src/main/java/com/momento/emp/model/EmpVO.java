package com.momento.emp.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "EMP")
public class EmpVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMP_ID")
    private Integer empId;

    @Column(name = "EMP_NAME", length = 50, nullable = false)
    private String empName;

    @Column(name = "ACCOUNT", length = 50, nullable = false, unique = true)
    private String account;

    @Column(name = "PASSWORD", length = 255, nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "STATUS", columnDefinition = "TINYINT DEFAULT 1")
    private Byte status; // 0:離職 1:在職

    // 關聯到權限 (選用，看是否需要雙向關聯)
    @OneToMany(mappedBy = "emp", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmpAuthority> authorities = new HashSet<>();

    // 無參數建構子
    public EmpVO() {
    }

    // 全參數建構子
    public EmpVO(Integer empId, String empName, String account, String password,
                 LocalDateTime createdAt, Byte status) {
        this.empId = empId;
        this.empName = empName;
        this.account = account;
        this.password = password;
        this.createdAt = createdAt;
        this.status = status;
    }

    // Getter & Setter
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

    public Set<EmpAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<EmpAuthority> authorities) {
        this.authorities = authorities;
    }

    // 便利方法：新增權限，維護雙向關聯
    public void addAuthority(EmpAuthority authority) {
        authorities.add(authority);
        authority.setEmp(this);
    }

    // 便利方法：移除權限
    public void removeAuthority(EmpAuthority authority) {
        authorities.remove(authority);
        authority.setEmp(null);
    }

    @Override
    public String toString() {
        return "Emp{" +
                "empId=" + empId +
                ", empName='" + empName + '\'' +
                ", account='" + account + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
