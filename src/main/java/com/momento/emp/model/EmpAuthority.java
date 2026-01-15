package com.momento.emp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "EMP_AUTHORITY")
@IdClass(EmpAuthorityId.class)
public class EmpAuthority {

    @Id
    @Column(name = "EMP_ID")
    private Integer empId;

    @Id
    @Column(name = "FUNCTION_ID")
    private Integer functionId;

    // 關聯對應
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID", insertable = false, updatable = false)
    private EmpVO emp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FUNCTION_ID", insertable = false, updatable = false)
    private BackendFunction function;

    // 無參數建構子
    public EmpAuthority() {
    }

    // 全參數建構子
    public EmpAuthority(Integer empId, Integer functionId) {
        this.empId = empId;
        this.functionId = functionId;
    }

    // Getter & Setter
    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public Integer getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Integer functionId) {
        this.functionId = functionId;
    }

    public EmpVO getEmp() {
        return emp;
    }

    public void setEmp(EmpVO emp) {
        this.emp = emp;
        if (emp != null) {
            this.empId = emp.getEmpId();
        }
    }

    public BackendFunction getFunction() {
        return function;
    }

    public void setFunction(BackendFunction function) {
        this.function = function;
        if (function != null) {
            this.functionId = function.getFunctionId();
        }
    }

    @Override
    public String toString() {
        return "EmpAuthorityVO{" +
                "empId=" + empId +
                ", functionId=" + functionId +
                '}';
    }
}