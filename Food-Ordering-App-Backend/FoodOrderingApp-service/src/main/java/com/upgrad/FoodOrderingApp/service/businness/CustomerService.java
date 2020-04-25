package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * This method implements the logic for 'signup' endpoint.
     *
     * @param customerEntity for creating new customer.
     * @return CustomerEntity object.
     * @throws SignUpRestrictedException if any of the validation fails.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        // if contactNumber is already registered throws exception with code SGR-001
        if (isContactNumberInUse(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }
        // checks the email entered by user is valid or not
        if (!isValidEmail(customerEntity.getEmailAddress())) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }
        // checks the contact number entered by user is valid or not
        if (!isValidContactNumber(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }
        // checks the password entered by user is valid or not
        if (!isValidPassword(customerEntity.getPassword())) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }
        customerEntity.setUuid(UUID.randomUUID().toString());
        // encrypts salt and password
        String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);
        return customerDao.saveCustomer(customerEntity);
    }

    /**
     * This method implements the logic for 'login' endpoint.
     *
     * @param username customers contactnumber will be the username.
     * @param password customers password.
     * @return CustomerAuthEntity object.
     * @throws AuthenticationFailedException if any of the validation fails.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(String username, String password) throws AuthenticationFailedException {
        if (username.isEmpty() || password.isEmpty() || !isValidContactNumber(username) || !isValidPassword(password)) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
        CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(username);
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (!encryptedPassword.equals(customerEntity.getPassword())) {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
        customerAuthEntity.setUuid(UUID.randomUUID().toString());
        customerAuthEntity.setCustomer(customerEntity);
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(8);
        customerAuthEntity.setLoginAt(now);
        customerAuthEntity.setExpiresAt(expiresAt);
        String accessToken = jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt);
        customerAuthEntity.setAccessToken(accessToken);

        customerDao.createCustomerAuthToken(customerAuthEntity);
        return customerAuthEntity;
    }

    // method checks for given contact number is already registered or not
    private boolean isContactNumberInUse(final String contactNumber) {
        return customerDao.getCustomerByContactNumber(contactNumber) != null;
    }

    // method checks for email format is correct or not
    private boolean isValidEmail(final String emailAddress) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-z A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(emailAddress).matches();
    }

    // method checks for given contact number is valid or not
    private boolean isValidContactNumber(final String contactNumber) {
        if (contactNumber.length() != 10) {
            return false;
        }
        for (int i = 0; i < contactNumber.length(); i++) {
            if (!Character.isDigit(contactNumber.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // method checks for given password meets the requirements or not
    private boolean isValidPassword(final String password) {
        return password.matches("^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[#@$%&*!^]).{8,}$");
    }
}