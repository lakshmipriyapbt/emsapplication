package com.pb.employee.request;


import com.pb.employee.config.ValidAge;
import com.sun.jersey.spi.StringReader;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.checkerframework.checker.i18nformatter.qual.I18nInvalidFormat;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidAge // <-- Apply the custom validation here
public class EmployeeRequest {

    @Schema(example = "companyShortName")
    @Pattern(regexp = "^[a-z]+$", message = "{companyName.format}")
    @NotBlank(message = "{companyname.message}")
    @Size(min = 2, max = 20, message = "{size.message}")
    private String companyName;

    @Schema(example = "employeeId")
    @Pattern(regexp = "^[a-zA-Z]+( [a-zA-Z]+)*$", message = "{employee.type}")
    @Size(min = 3, max = 20, message = "{employeeType.size.message}")
    private String employeeType;

    @Schema(example = "employeeId")
    @Size(min = 2, max = 20, message = "{employeeId.size.message}")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "{employeeId.format}")
    private String employeeId;

    @Schema(example = "firstName")
    @Pattern(regexp ="^[A-Z][a-z]+(?:\\s[A-Z][a-z]+)*$", message = "{firstname.format}")
    @Size(min = 3, max = 20, message = "{firstName.size.message}")
    private String firstName;

    @Schema(example = "lastName")
    @Pattern(regexp = "^[A-Z](?:[a-z]+)?(?:\\s[A-Z][a-z]+)*$", message = "{lastname.format}")
    @Size(min = 1, max = 20, message = "{lastName.size.message}")
    private String lastName;

    @Schema(example = "emailId")
    @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$", message = "{invalid.emailId}")
    @NotBlank(message = "{emailId.notnull.message}")
    private String emailId;// "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"

    @Schema(example = "password")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{6,16}$", message = "{invalid.password}")
    @NotBlank(message = "{password.notnull.message}")
    private String password;

    @Schema(example = "designationId")
//    @Pattern(regexp = "^(?!.*[\\s]{2})(?!.*\\s$)(?!^\\s)(?!.*\\d.*)[A-Za-z]+(?:\\s[A-Za-z]+)*$", message = "{designation.format}")
    @Size(min = 2, max = 100, message = "{designation.size.message}")
    private String designation;

    @Schema(example = "yyyy-mm-dd")
    @Pattern(regexp =  "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "{dateOfHiring.format}")
    @NotBlank(message = "{dateOfHiring.notnull.message}")
    private String dateOfHiring;

    @Schema(example = "yyyy-mm-dd")
    @Pattern(regexp =  "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "{dateOfBirth.format}")
    @NotBlank(message = "{dateOfBirth.notnull.message}")
    private String dateOfBirth;

    @Schema(example = "departmentId")
//    @Pattern(regexp = "^(?!.*[\\s]{2})(?!.*\\s$)(?!^\\s)(?!.*\\d.*)[A-Za-z]+(?:\\s[A-Za-z]+)*$", message = "{department.format}")
    @Size(min = 2, max = 100, message = "{department.size.message}")
    private String department;

    @Schema(example = "location")
    @Pattern(regexp = "^[\\w\\s'.#&*()^\\-/]*[A-Za-z0-9]+(\\s+[A-Z][a-zA-Z0-9'.#&*()^\\-/]*)*,?\\s*[A-Z]*[a-zA-Z]*\\s*(\\d{6})?$", message = "{location.format}")
    @Size(min = 2, max = 100, message = "{location.notnull.message}")
    private String location;

    @Schema(example = "manager")
    @Pattern(regexp = "^(?!.*[\\s]{2})(?!.*\\s$)(?!^\\s)(?!.*\\d.*)[A-Z][a-z]+(?:\\s[A-Z][a-z]+)*$", message = "{manager.format}")
    @Size(min = 3, max = 30, message = "{manager.notnull.message}")
    private String manager;

   /* @NotNull(message = "{roles.format}")
    @Size(min = 1, message = "{roles.size}")
    private List<@NotBlank(message = "{role.notnull.message}")
    @Pattern(regexp = "^[A-Z][a-z]+$", message = "{roles.format}") String> roles;*/

    @Schema(example = "status")//
    @Pattern(regexp = "^[A-Za-z]+(?:\\s[A-Za-z]+)*$", message = "{status.format}")
    @NotBlank(message = "{status.notnull.message}")
    private String status;


    @Schema(example = "panNo")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "{invalid.panNo}")
    @NotBlank(message = "{panNo.notnull.message}")
    private String panNo;

    @Schema(example = "uanNo")
    @Pattern(regexp = "^\\d{12}$", message = "{invalid.uanNo}")
    @NotBlank(message = "{uanNo.notnull.message}")
    private String uanNo;

    @Schema(example = "aadhaarId")
    @Pattern(regexp = "^\\d{12}$", message = "{invalid.aadhaarId}")
    @NotBlank(message = "{aadhaarId.notnull.message}")
    private String aadhaarId;

    @Schema(example = "accountNo")
    @Pattern(regexp = "^\\d{9,18}$", message = "{accountNo.format}")
    @NotBlank(message = "{accountNo.notnull.message}")
    private String accountNo;

    @Schema(example = "ifscCode")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "{ifscCode.format}")
    @NotBlank(message = "{ifscCode.notnull.message}")
    private String ifscCode;

    @Schema(example = "bankName")
    @Pattern(regexp = "^(?!.*\\s{2})(?!.*\\s$)(?!^\\s)([A-Z]+[a-z]*)(?:\\s[A-Za-z]+)*$", message = "{bankName.format}")
    @Size(min = 3, max = 20, message = "{bankName.size.message}")
    private String bankName;

}