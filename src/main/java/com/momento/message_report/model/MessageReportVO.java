package com.momento.message_report.model;

import java.sql.Timestamp;

import com.momento.emp.model.EmpVO;
import com.momento.member.model.MemberVO;
import com.momento.message.model.MessageVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "MESSAGE_REPORT")
public class MessageReportVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Integer messageReportId;
	private MessageVO messageVO;
	private MemberVO memberVO;
	private EmpVO empVO;
	private Timestamp reportedAt;
	private String reportReason;
	private Integer status;

	public MessageReportVO() {
	}

	@Id
	@Column(name = "MESSAGE_REPORT_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getMessageReportId() {
		return this.messageReportId;
	}

	public void setMessageReportId(Integer messageReportId) {
		this.messageReportId = messageReportId;
	}

	@ManyToOne
	@JoinColumn(name = "MESSAGE_ID")
	public MessageVO getMessageVO() {
		return this.messageVO;
	}

	public void setMessageVO(MessageVO messageVO) {
		this.messageVO = messageVO;
	}

	@ManyToOne
	@JoinColumn(name = "MEMBER_ID")
	public MemberVO getMemberVO() {
		return this.memberVO;
	}

	public void setMemberVO(MemberVO memberVO) {
		this.memberVO = memberVO;
	}

	@ManyToOne
	@JoinColumn(name = "EMP_ID")
	public EmpVO getEmpVO() {
		return this.empVO;
	}

	public void setEmpVO(EmpVO empVO) {
		this.empVO = empVO;
	}

	@Column(name = "REPORTED_AT", insertable = false, updatable = false)
	public Timestamp getReportedAt() {
		return this.reportedAt;
	}

	public void setReportedAt(Timestamp reportedAt) {
		this.reportedAt = reportedAt;
	}

	@Column(name = "REPORT_REASON")
	@NotEmpty(message="檢舉理由: 請勿空白")
	@Size(max=500, message="檢舉理由: 長度不能超過{max}")
	public String getReportReason() {
		return this.reportReason;
	}

	public void setReportReason(String reportReason) {
		this.reportReason = reportReason;
	}

	@Column(name = "STATUS")
	@NotNull(message="狀態: 請勿空白")
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
