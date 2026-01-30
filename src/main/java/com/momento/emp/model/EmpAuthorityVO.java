package com.momento.emp.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "EMP_AUTHORITY")
public class EmpAuthorityVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTHORITY_ID")
    private Integer authorityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID", nullable = false)
    private EmpVO emp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FUNCTION_ID", nullable = false)
    private BackendFunctionVO function;

    public EmpAuthorityVO() {
    }

    public EmpAuthorityVO(EmpVO emp, BackendFunctionVO function) {
        this.emp = emp;
        this.function = function;
    }

    public Integer getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Integer authorityId) {
        this.authorityId = authorityId;
    }

    public EmpVO getEmp() {
        return emp;
    }

    public void setEmp(EmpVO emp) {
        this.emp = emp;
    }

    public BackendFunctionVO getFunction() {
        return function;
    }

    public void setFunction(BackendFunctionVO function) {
        this.function = function;
    }

    // 用於方便取得 ID 的 Helper 方法
    public Integer getEmpId() {
        return emp != null ? emp.getEmpId() : null;
    }

    public Integer getFunctionId() {
        return function != null ? function.getFunctionId() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EmpAuthorityVO))
            return false;
        EmpAuthorityVO that = (EmpAuthorityVO) o;
        return Objects.equals(getEmpId(), that.getEmpId()) &&
                Objects.equals(getFunctionId(), that.getFunctionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmpId(), getFunctionId());
    }

    @Override
    public String toString() {
        return "EmpAuthorityVO{" +
                "authorityId=" + authorityId +
                ", empId=" + getEmpId() +
                ", functionId=" + getFunctionId() +
                '}';
    }
}