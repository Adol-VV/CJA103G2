package com.momento.event.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TYPE")
public class TypeVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TYPE_ID")
    private Integer typeId;

    @Column(name = "TYPE_NAME", nullable = false, length = 50)
    private String typeName;

    @Column(name = "NOTE", length = 200)
    private String note;

    // ========== Constructors ==========

    public TypeVO() {
    }

    public TypeVO(String typeName, String note) {
        this.typeName = typeName;
        this.note = note;
    }

    // ========== Getters & Setters ==========

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}