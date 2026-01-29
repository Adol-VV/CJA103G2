package com.momento.organizer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 主辦方DTO
 * 封裝註冊表單資料，與資料庫實體 (VO) 分離>>>提升安全性。
 */
public class OrganizerApplyDTO {

    @NotBlank(message = "帳號不能為空")
    @Size(min = 4, max = 50, message = "帳號長度需在 4 到 50 字元之間")
    private String account;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 8, message = "密碼長度至少需 8 字元")
    private String password;

    @NotBlank(message = "請再次輸入密碼")
    private String confirmPassword;

    @NotBlank(message = "單位名稱不能為空")
    @Size(max = 100, message = "單位名稱長度上限為 100 字元")
    private String name;

    @NotBlank(message = "負責人姓名不能為空")
    @Size(max = 50, message = "負責人姓名長度上限為 50 字元")
    private String ownerName;

    @NotBlank(message = "聯絡電話不能為空")
    @Size(min = 7, max = 20, message = "電話長度需在 7 到 20 字元之間")
    private String phone;

    @NotBlank(message = "Email 不能為空")
    @Email(message = "Email 格式不正確")
    private String email;

    private String introduction;

    @NotBlank(message = "銀行代碼不能為空")
    @Pattern(regexp = "^\\d{3}$", message = "銀行代碼應為 3 位數字")
    private String bankCode;

    @NotBlank(message = "銀行帳號不能為空")
    @Pattern(regexp = "^\\d{10,16}$", message = "銀行帳號格式不正確")
    private String bankAccount;

    @NotBlank(message = "戶名不能為空")
    private String accountName;

    // Getters and Setters
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
