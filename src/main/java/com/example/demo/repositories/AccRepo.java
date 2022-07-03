package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.Account;

@RepositoryRestResource(collectionResourceRel = "account")
public interface AccRepo extends JpaRepository<Account, Integer>{

	public List<Account> findByActive(String active);
	public List<Account> findByAmountBetweenOrderByAmount(int min, int max);
	public List<Account> getByBank(String bank);
	public List<Account> getByType(String type);
	public List<Account> findByAccountno(int accountno);
	
	@Transactional
	@Modifying
	@Query("update Account a set a.active='no' where a.accountno=?1")
	public void disableAccount(int accountno);
	
	@Transactional
	@Modifying
	@Query("update Account a set a.amount=a.amount-?2 where a.accountno=?1")
	public void amountTransaction(int fromAcct, int amt);
	
	@Transactional
	@Modifying
	@Query("update Account a set a.amount=a.amount+?2 where a.accountno=?1")
	public void amountReceive( int toAcct, int amt);
	
	@Transactional
	@Modifying
	@Query("update Account a set a.amount=a.amount-200-?2 where a.accountno=?1")
	public void minBalancePenalty(int fromAcct, int amt);
	
}
