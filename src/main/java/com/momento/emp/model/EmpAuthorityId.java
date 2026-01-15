package com.momento.emp.model;

import java.io.Serializable;
import java.util.Objects;

public class EmpAuthorityId implements Serializable {

    private Integer empId;
    private Integer functionId;

    public EmpAuthorityId() {
    }

    public EmpAuthorityId(Integer empId, Integer functionId){
        this.empId = empId;
        this.functionId = functionId;
    }

    public Integer getEmpId(){
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

    @Override
    public boolean equals(Object o){

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EmpAuthorityId that = (EmpAuthorityId) o;
        return Objects.equals(empId,that.empId) &&
                Objects.equals(functionId, that.functionId);
    }

    @Override
    public int hashCode(){
        return  Objects.hash(empId, functionId);
    }

}

