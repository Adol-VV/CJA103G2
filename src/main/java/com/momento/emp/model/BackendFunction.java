package com.momento.emp.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "`FUNCTION`")
public class BackendFunction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FUNCTION_ID")
    private Integer functionId;

    @Column(name = "FUNCTION_NAME", length = 50, nullable = false)
    private String functionName;

    // 關聯到權限 (選用)
    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmpAuthority> authorities = new HashSet<>();

    // 無參數建構子
    public BackendFunction() {
    }

    // 全參數建構子
    public BackendFunction(Integer functionId, String functionName) {
        this.functionId = functionId;
        this.functionName = functionName;
    }

    // Getter & Setter
    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Set<EmpAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<EmpAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "BackendFunction{" +
                "functionId=" + functionId +
                ", functionName='" + functionName + '\'' +
                '}';
    }
}
