package com.momento.member.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "member")
public class MemberVO implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id", updatable = false)
	private Integer memberId;

	@NotBlank(message = "電子信箱請勿空白")
    @Email(message = "電子信箱格式錯誤")
    @Column(name = "account", nullable = false, unique = true)
    private String account;

	@Column(name = "password", nullable = false)
	@Size(min=8, message="長度須為8碼以上")
	private String password;

	@Column(name = "name", nullable = false)
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z)]{1,10}$", message = "必須是中文或英文 , 且長度在1到10之間")
	private String name;
	
	@NotBlank(message = "地址請勿空白")
	@Column(name = "address")
	private String address;

	@Column(name = "phone")
	@Pattern(regexp = "^(09)[0-9]{8}$", message = "手機號碼格式不符")
	private String phone;

	@Column(name = "token")
	private Integer token;

	@Column(name = "status")
	private Integer status;

	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	public MemberVO() {
		super();
	}

	public MemberVO(Integer memberId, String account, String password, String name, String address, String phone,
			Integer token, Integer status, LocalDateTime createdAt) {
		super();
		this.memberId = memberId;
		this.account = account;
		this.password = password;
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.token = token;
		this.status = status;
		this.createdAt = createdAt;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getToken() {
		return token;
	}

	public void setToken(Integer token) {
		this.token = token;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "MemberVO [memberId=" + memberId + ", account=" + account + ", password=" + password + ", name=" + name
				+ ", address=" + address + ", phone=" + phone + ", token=" + token + ", status=" + status
				+ ", createdAt=" + createdAt + "]";
	}

}
