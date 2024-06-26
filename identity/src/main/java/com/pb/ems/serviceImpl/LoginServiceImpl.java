package com.pb.ems.serviceImpl;

import com.pb.ems.auth.JwtTokenUtil;
import com.pb.ems.common.ResponseBuilder;
import com.pb.ems.exception.ErrorMessageHandler;
import com.pb.ems.exception.IdentityErrorMessageKey;
import com.pb.ems.exception.IdentityException;
import com.pb.ems.model.*;
import com.pb.ems.opensearch.OpenSearchOperations;
import com.pb.ems.persistance.Entity;
import com.pb.ems.service.LoginService;
import com.pb.ems.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private OpenSearchOperations openSearchOperations;
    /**
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<?> login(LoginRequest request) throws IdentityException {
        try{
            EmployeeEntity user = openSearchOperations.getEMSAdminById(request.getUsername());
            if(user != null && user.getPassword() != null) {
                String password = new String(Base64.getDecoder().decode(user.getPassword()), StandardCharsets.UTF_8);
                if(request.getPassword().equals(password)) {
                    log.debug("Successfully logged into ems portal for {}", request.getUsername());
                } else {
                    log.error("Invalid credentials");
                    throw new IdentityException(ErrorMessageHandler.getMessage(IdentityErrorMessageKey.INVALID_CREDENTIALS),
                            HttpStatus.FORBIDDEN);
                }
            } else {
                log.error("Invalid credentials");
                throw new IdentityException(ErrorMessageHandler.getMessage(IdentityErrorMessageKey.INVALID_CREDENTIALS),
                        HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            log.error("Invalid creds {}", e.getMessage(),e);
            throw new IdentityException(ErrorMessageHandler.getMessage(IdentityErrorMessageKey.INVALID_CREDENTIALS),
                    HttpStatus.FORBIDDEN);
        }
        List<String> roles = new ArrayList<>();
        roles.add(Constants.EMS_ADMIN);
        String token = JwtTokenUtil.generateToken(request.getUsername(), roles);
        return new ResponseEntity<>(
                ResponseBuilder.builder().build().createSuccessResponse(new LoginResponse(token, null)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> employeeLogin(EmployeeLoginRequest request) throws IdentityException {
        EmployeeEntity employee;
        try{
            employee = openSearchOperations.getEmployeeById(request.getUsername(), request.getCompany());
            if(employee != null && employee.getPassword() != null) {

        String company = request.getCompany();
        try {
            employee = openSearchOperations.getEmployeeById(request.getUsername(), request.getCompany());
            System.out.println("The employee details is : "+employee.getEmployeeId());
            if (employee != null && employee.getPassword() != null) {

                String password = new String(Base64.getDecoder().decode(employee.getPassword()), StandardCharsets.UTF_8);
                if(request.getPassword().equals(password)) {
                    log.debug("Successfully logged into ems portal for {}", request.getUsername());
                } else {
                    log.error("Invalid credentials");
                    throw new IdentityException(ErrorMessageHandler.getMessage(IdentityErrorMessageKey.INVALID_CREDENTIALS),
                            HttpStatus.FORBIDDEN);
                }
            } else {
                log.error("Invalid credentials");
                throw new IdentityException(ErrorMessageHandler.getMessage(IdentityErrorMessageKey.INVALID_CREDENTIALS),
                        HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            log.error("Invalid creds {}", e.getMessage(),e);
            throw new IdentityException(ErrorMessageHandler.getMessage(IdentityErrorMessageKey.INVALID_CREDENTIALS),
                    HttpStatus.FORBIDDEN);
        }

        Long otp = generateOtp();
        sendOtpByEmail(request.getUsername(), otp);
        openSearchOperations.saveOtpToUser(employee, otp,company);
        List<String> roles = new ArrayList<>();
        if(employee != null && employee.getRoles() != null && employee.getRoles().size() > 0) {
            roles.addAll(employee.getRoles());
        } else {
            roles.add(Constants.COMPANY_ADMIN);
        }

        String token = JwtTokenUtil.generateToken(request.getUsername(), roles);
        return new ResponseEntity<>(
                ResponseBuilder.builder().build().createSuccessResponse(new LoginResponse(token, null)), HttpStatus.OK);
    }

    public void sendOtpByEmail(String emailId, Long otp) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emailId);
        mailMessage.setSubject("${mail.subject}");
        String mailText = "${mail.text}";
        String formattedText = mailText.replace("{emailId}", emailId).replace("{otp}", otp.toString());

        mailMessage.setText(formattedText);
        javaMailSender.send(mailMessage);
        log.info("OTP sent successfully....");//otp is send succesfully...
    }

    /**
     * @param loginRequest
     * @return
     */
    @Override
    public ResponseEntity<?> logout(OTPRequest loginRequest) {
        return null;
    }


    @Override
    public ResponseEntity<?> validateCompanyOtp(OTPRequest request) throws IdentityException {
        EmployeeEntity user;
        try {
            log.debug("Validating OTP for user: " + request.getUsername() + " and company: " + request.getCompany());
            user = openSearchOperations.getEmployeeById(request.getUsername(), request.getCompany());

            if (user == null) {
                log.error("No user found for username: " + request.getUsername() + " and company: " + request.getCompany());
            } else {
                log.debug("User details: " + user.toString());
            }

            if (user != null && user.getOtp() != null) { //1718988801
                Long otp = user.getOtp();
                long currentTime = Instant.now().plus(0,ChronoUnit.SECONDS).getEpochSecond();

                if (!request.getOtp().equals(otp)) {
                    log.error("Invalid OTP for user: " + request.getUsername());
                    throw new IdentityException(ErrorMessageHandler.getMessage(IdentityErrorMessageKey.INVALID_OTP),
                            HttpStatus.FORBIDDEN);
                }

                if (currentTime > user.getExpiryTime()) {
                    log.error("OTP expired for user: " + request.getUsername());
                    throw new IdentityException(ErrorMessageHandler.getMessage(IdentityErrorMessageKey.OTP_EXPIRED),
                            HttpStatus.FORBIDDEN);
                }
                log.debug("OTP is valid for user: " + request.getUsername());
            } else {
                log.error("Invalid credentials for user: " + request.getUsername());
                throw new IdentityException(ErrorMessageHandler.getMessage(IdentityErrorMessageKey.INVALID_CREDENTIALS),
                        HttpStatus.FORBIDDEN);
            }
        } catch (IdentityException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error validating OTP for user: " + request.getUsername(), e);
            throw new IdentityException(ErrorMessageHandler.getMessage(IdentityErrorMessageKey.INVALID_CREDENTIALS),
                    HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(
                ResponseBuilder.builder().build().createSuccessResponse(new LoginResponse()), HttpStatus.OK);
    }




    private Long generateOtp() {
        Random random = new Random();
        Long otp = 100000 + random.nextLong(900000);
        return otp;
    }

    @Override
    public ResponseEntity<?> updateEmsAdmin(LoginRequest request) throws IdentityException {
        try{
            EmployeeEntity user = openSearchOperations.getEMSAdminById(request.getUsername());
            if(user == null ) {
                throw new IdentityException(ErrorMessageHandler.getMessage(IdentityErrorMessageKey.INVALID_USERNAME),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex ){
            log.error("Exception while fetching user {}, {}", request.getUsername(), ex);
            throw new IdentityException(ErrorMessageHandler.getMessage(IdentityErrorMessageKey.INVALID_USERNAME),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String id = Constants.EMS_ADMIN+"_"+request.getUsername();
        Entity entity = EmployeeEntity.builder().
                emailId(request.getUsername()).
                password(request.getPassword()).build();
        openSearchOperations.saveEntity(entity, id, Constants.INDEX_EMS);
        return new ResponseEntity<>(
                ResponseBuilder.builder().build().createSuccessResponse(Constants.SUCCESS), HttpStatus.OK);
    }
}