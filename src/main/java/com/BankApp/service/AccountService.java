package com.BankApp.service;

import com.BankApp.model.Account;
import com.BankApp.model.Transaction;
import com.BankApp.respository.AccountRepository;
import com.BankApp.respository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder ;

    @Autowired
    private AccountRepository accountRepository ;

   @Autowired
   private  TransactionRepository transactionRepository ;
    public Account findAccountByUsername(String userName) {
        return accountRepository.findByUsername(userName).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public void registerAccount(String userName , String password) {
        String hashedPassword = passwordEncoder.encode(password);
         if(accountRepository.findByUsername(userName).isPresent()) {
             throw new RuntimeException("User already exists");
         }
         Account account = new Account();
         account.setUsername(userName);
        account.setPassword(hashedPassword);
         account.setBalance(BigDecimal.ZERO);
        System.out.println("Registered user: " + account.getUsername() + ", password (encoded): " + account.getPassword());
        accountRepository.save(account);
    }

    public boolean login(String username, String password) {
        Optional<Account> optional = accountRepository.findByUsername(username);

        if (optional.isEmpty()) {
            System.out.println("NOT FOUND in DB for username: " + username);
            return false;
        }

        Account account = optional.get();

        System.out.println("RAW password: " + password);
        System.out.println("STORED encoded: " + account.getPassword());

        boolean matched = passwordEncoder.matches(password, account.getPassword());
        System.out.println("Password matched: " + matched);

        return true;
    }




    public void deposit(Account account , BigDecimal amount) {
       account.setBalance(account.getBalance().add(amount));
       accountRepository.save(account) ;
        Transaction transaction = new Transaction(amount , "Deposit" , LocalDateTime.now(),account) ;
         transactionRepository.save(transaction) ;
    }

    public void withdrawl(Account account , BigDecimal amount) {
           if(account.getBalance().compareTo(amount) < 0) {
             throw  new RuntimeException("insufficient funds");
           }
           account.setBalance(account.getBalance().subtract(amount));
           accountRepository.save(account) ;

        Transaction transaction = new Transaction(amount , "Withdrawl" , LocalDateTime.now(),account) ;
        transactionRepository.save(transaction) ;
    }

    public List<Transaction> getTransactionHistory(Account account) {
        return transactionRepository.findByAccountId(account.getId());
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Account account  = findAccountByUsername(userName);
        if(account == null) {
            throw  new UsernameNotFoundException("UserName and password not found") ;
        }
        return new Account(
             account.getUsername(),
             account.getPassword(),
                account.getBalance(),
                account.getTransaction(),
                authorities()
        );

    }
    public Collection<? extends GrantedAuthority> authorities() {
           return List.of(new SimpleGrantedAuthority("user"));
    }
    public void tranferAmount(Account fromAccount , String toUserName , BigDecimal amount){
          if(fromAccount.getBalance().compareTo(amount)< 0) {
           throw  new RuntimeException("insufficient funds");
          }
          Account toAccount = accountRepository.findByUsername(toUserName).orElseThrow(()-> new RuntimeException("Recipient account not found"));

          //decuct
         fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
         accountRepository.save(fromAccount);

         // add
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(toAccount);

        // transaction record

        Transaction debitTransaction = new Transaction(
                amount ,
                "Transfer out to" + toAccount.getUsername(),
                LocalDateTime.now(),
                fromAccount
        );
     transactionRepository.save(debitTransaction);

     // credit transaction
        Transaction creditTransaction = new Transaction(
                amount ,
                "Transfer In to" + fromAccount.getUsername(),
                LocalDateTime.now(),
                toAccount
        );
        transactionRepository.save(creditTransaction);
        
    }
}
