package com.momento.emp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "EMP_AUTHORITY")
public class EmpAuthorityVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTHORITY_ID")
    private Integer authorityId;

    @Column(name = "EMP_ID",nullable = false)
    private Integer empId;

    @Column(name = "FUNCTION_ID", nullable = false)
    private Integer functionId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID", insertable = false, updatable = false)
    private EmpVO emp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FUNCTION_ID", insertable = false, updatable = false)
    private BackendFunction function;

    public EmpAuthorityVO() {
    }

    public EmpAuthorityVO(Integer empId, Integer functionId) {
        this.empId = empId;
        this.functionId = functionId;
    }

    public Integer getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Integer authorityId) {
        this.authorityId = authorityId;
    }

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
                "authorityId=" + authorityId +
                ", empId=" + empId +
                ", functionId=" + functionId +
                '}';
    }
}