package com.ewe.serviceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ewe.config.PhonePeConfig;
import com.ewe.dao.GeneralDao;
import com.ewe.pojo.AccountTransactions;
import com.ewe.pojo.Accounts;
import com.ewe.pojo.PhonePeOrderStatusResponse;
import com.ewe.pojo.PhonePePaymentRequest;
import com.ewe.pojo.PhonePePaymentResponse;
import com.ewe.pojo.PhonePeTokenResponse;
import com.ewe.pojo.User;
import com.ewe.service.PhonePeService;

@Service
public class PhonePeServiceImpl implements PhonePeService {

    private final PhonePeConfig phonePeConfig;
    private final RestTemplate restTemplate;
    private final GeneralDao generalDao;

    public PhonePeServiceImpl(PhonePeConfig phonePeConfig,
                               RestTemplate restTemplate,
                               GeneralDao generalDao) {
        this.phonePeConfig = phonePeConfig;
        this.restTemplate = restTemplate;
        this.generalDao = generalDao;
    }

    @Override
    public PhonePeTokenResponse generateToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + phonePeConfig.getClientId()
                + "&client_version=" + phonePeConfig.getClientVersion()
                + "&client_secret=" + phonePeConfig.getClientSecret()
                + "&grant_type=client_credentials";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<PhonePeTokenResponse> response = restTemplate.exchange(
                    phonePeConfig.getAuthUrl(),
                    HttpMethod.POST,
                    entity,
                    PhonePeTokenResponse.class
            );
            System.out.println("Token Success");
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Token Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public PhonePePaymentResponse createPayment(PhonePePaymentRequest request) {

        PhonePeTokenResponse tokenResponse = generateToken();
        String token = tokenResponse.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "O-Bearer " + token);

        Map<String, Object> paymentFlow = new HashMap<>();
        paymentFlow.put("type", "PG_CHECKOUT");
        paymentFlow.put("message", "Payment for order " + request.getMerchantOrderId());

        Map<String, String> merchantUrls = new HashMap<>();
        merchantUrls.put("redirectUrl", phonePeConfig.getRedirectUrl());
        paymentFlow.put("merchantUrls", merchantUrls);

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("merchantOrderId",     request.getMerchantOrderId());
        bodyMap.put("amount",              request.getAmount());
        bodyMap.put("expireAfter",         request.getExpireAfter() != null ? request.getExpireAfter() : 1200);
        bodyMap.put("paymentFlow",         paymentFlow);
        bodyMap.put("disablePaymentRetry", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bodyMap, headers);

        ResponseEntity<PhonePePaymentResponse> response = restTemplate.exchange(
                phonePeConfig.getPayUrl(),
                HttpMethod.POST,
                entity,
                PhonePePaymentResponse.class
        );

        PhonePePaymentResponse phonePeResponse = response.getBody();

        try {
            if (request.getUserId() != null && phonePeResponse != null) {

                // ✅ findOneSQLQuery(T newsEntry, String query) — your exact method signature
                String userQuery = "SELECT * FROM users WHERE id = " + request.getUserId();
                User user = (User) generalDao.findOneSQLQuery(new User(), userQuery);

                if (user == null) {
                    System.out.println("User not found: " + request.getUserId());
                } else {

                    // ✅ TOP 1 in SQL — prevents NonUniqueResultException
                    String accountQuery = "SELECT TOP 1 * FROM accounts WHERE user_id = "
                            + user.getId() + " ORDER BY id ASC";
                    Accounts account = (Accounts) generalDao.findOneSQLQuery(new Accounts(), accountQuery);

                    if (account == null) {
                        System.out.println("Account not found for user: " + user.getId());
                    } else {
                        // Save new PENDING transaction
                        AccountTransactions transaction = new AccountTransactions();
                        transaction.setTransactionId(request.getMerchantOrderId());
//                        transaction.setTransactionId(phonePeResponse.getOrderId());

                        transaction.setAmtCredit(0);
                        transaction.setAmtDebit(0);
                        transaction.setCurrentBalance(account.getAccountBalance());
                        transaction.setStatus("PENDING");
                        transaction.setComment("PhonePe payment initiated - Rs." + (request.getAmount() / 100));
                        transaction.setAccount(account);
                        transaction.setCreationDate(new Date());

                        // ✅ save() — new row for each payment
                        generalDao.save(transaction);
                        System.out.println("PENDING transaction saved: " + request.getMerchantOrderId());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Warning - Could not save pending transaction: " + e.getMessage());
            e.printStackTrace();
        }

        return phonePeResponse;
    }

    @Override
    public PhonePeOrderStatusResponse checkOrderStatus(String merchantOrderId) {

        PhonePeTokenResponse tokenResponse = generateToken();
        String token = tokenResponse.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "O-Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = phonePeConfig.getStatusUrl() + "/" + merchantOrderId + "/status?details=false";
        System.out.println("Checking status: " + url);

        try {
            ResponseEntity<PhonePeOrderStatusResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    PhonePeOrderStatusResponse.class
            );
            System.out.println("Order Status: " + response.getBody().getState());
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Status Check Error: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public PhonePeOrderStatusResponse verifyAndUpdateWallet(String merchantOrderId) {

        PhonePeOrderStatusResponse statusResponse = checkOrderStatus(merchantOrderId);
        String state = statusResponse.getState();
        System.out.println("Payment state: " + state);

        try {
            // Step 1: Find PENDING transaction — TOP 1 prevents NonUniqueResultException
            String txnQuery = "SELECT TOP 1 * FROM account_transaction WHERE transactionId = '"
                    + merchantOrderId + "' AND status = 'PENDING' ORDER BY id ASC";
            AccountTransactions transaction = (AccountTransactions) generalDao
                    .findOneSQLQuery(new AccountTransactions(), txnQuery);

            System.out.println("Transaction found: " + (transaction != null));

            if (transaction == null) {
                System.out.println("No PENDING transaction found: " + merchantOrderId);
                return statusResponse;
            }

            // Step 2: Find account — TOP 1 prevents NonUniqueResultException
            String accountQuery = "SELECT TOP 1 * FROM accounts WHERE id = "
                    + "(SELECT TOP 1 account_id FROM account_transaction "
                    + "WHERE transactionId = '" + merchantOrderId + "' ORDER BY id ASC)";
            Accounts account = (Accounts) generalDao
                    .findOneSQLQuery(new Accounts(), accountQuery);

            System.out.println("Account found: " + (account != null ? account.getId() : "NULL"));
            System.out.println("Current balance: " + (account != null ? account.getAccountBalance() : "NULL"));

            if (account == null) {
                System.out.println("Account not found: " + merchantOrderId);
                return statusResponse;
            }

            if ("COMPLETED".equals(state)) {

                double amountInRupees = statusResponse.getAmount() / 100.0;
                double newBalance = account.getAccountBalance() + amountInRupees;

                System.out.println("Amount credited : Rs." + amountInRupees);
                System.out.println("New balance     : Rs." + newBalance);

                // ✅ savOrupdate — your exact method name (not saveOrupdate)
                account.setAccountBalance(newBalance);
                generalDao.savOrupdate(account);
                System.out.println("Account balance updated!");

                // ✅ savOrupdate — updates existing transaction row
                transaction.setAmtCredit(amountInRupees);
                transaction.setAmtDebit(0);
                transaction.setCurrentBalance(newBalance);
                transaction.setStatus("COMPLETED");
                transaction.setComment("PhonePe payment successful - Rs." + amountInRupees);
                generalDao.savOrupdate(transaction);
                System.out.println("Transaction COMPLETED!");

            } else if ("FAILED".equals(state)) {

                transaction.setStatus("FAILED");
                transaction.setComment("PhonePe payment failed");
                generalDao.savOrupdate(transaction);
                System.out.println("Transaction FAILED!");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return statusResponse;
    }
}