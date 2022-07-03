package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Account;
import com.example.demo.repositories.AccRepo;

@RestController
public class MyRestController {

	@Autowired
	AccRepo repo;

	@GetMapping("/accounts")
	public List<Account> getAllAccounts() {
		return repo.findAll();
	}

	@PostMapping("/transaction/transfer")
	public String transaction(@RequestParam("fromAcct") int fromAcct, @RequestParam("toAcct") int toAcct,
			@RequestParam("amt") int amt, @RequestParam("ifsccode") String ifsccode) {

		if (repo.existsById(fromAcct) && repo.existsById(toAcct)) {
			List<Account> list1 = repo.findByAccountno(fromAcct);
			List<Account> list2 = repo.findByAccountno(toAcct);
			if (list1.get(0).getActive().equals("yes") && list2.get(0).getActive().equals("yes")) {
				if (list2.get(0).getIfsccode().equalsIgnoreCase(ifsccode)) {
					if (list1.get(0).getAmount() >= amt) {
						if (list1.get(0).getAmount() >= amt + 5000) {
							repo.amountTransaction(fromAcct, amt);
							repo.amountReceive(toAcct, amt);
							return "Transaction SUCCESSFUL!";
						}
						// else if(list1.getAmount() < amt+5000)
						else {
							if ((list1.get(0).getAmount() - amt - 200) >= 1) {
								repo.minBalancePenalty(fromAcct, amt);
								repo.amountReceive(toAcct, amt);
								return "Transaction SUCCESSFUL! Rs.200 deducted for not maintaining the minimum account balance...";
								// return " "+(list1.get(0).getAmount()-amt-200);
							}
							// else if (list1.getAmount()-200 < 0)
							else {
								repo.amountTransaction(fromAcct, amt);
								repo.amountReceive(toAcct, amt);
								return "Transaction SUCCESSFUL!";
							}
						}
					} else {
						return "INSUFFICIENT BALANCE";
					}
				} else {
					return "IFSC Code didn't match";
				}
			} else {
				return "ACCOUNT INACTIVE";
			}
		} else {
			return "TRANSACTION UNSUCCESSFUL!";
		}

	}

	@GetMapping("/loan/emi")
	public String loanEmi(@RequestParam("name") String name, @RequestParam("amount") int amount, @RequestParam("tenure") int tenure, @RequestParam("type") String type, @RequestParam("employed") String employed)
	{
		int roi=0;
		double emi=0;
		int maxamt=0;
		int maxtime=0;
		double memi;
		if(name.equals(null))
		{
			return "Enter the name of the applicant";
		}
		if(type.equalsIgnoreCase("home"))
		{
			roi=7;
			maxamt=10000000;
			maxtime=25;
			if(employed.equalsIgnoreCase("false"))
			{
				roi=roi+1;
			}
		}
		else if(type.equalsIgnoreCase("car"))
		{
			roi=9;
			maxamt=2000000;
			maxtime=7;
			if(employed.equalsIgnoreCase("false"))
			{
				roi=roi+1;
			}
		}
		else if(type.equalsIgnoreCase("personal"))
		{
			roi=12;
			maxamt=1000000;
			maxtime=5;
			if(employed.equalsIgnoreCase("false"))
			{
				roi=roi+1;
			}
		}
		else {
			return "Please enter a valid loan type";
		}
			if((amount>maxamt) || (amount<=0))
			{
				return " Invalid Loan Amount for "+type+" loans";
			}
			if((tenure>maxtime) || (tenure<=0))
			{
				return "Invalid loan duration for "+type+" loans";
			}
			
//			EMI = [P x R x (1+R)^N]/[(1+R)^N-1]
			//memi=(amount*roi*(1+roi)^tenure)/((1+roi)^(tenure-1));
			emi=(amount*tenure*roi)/100;
			memi=emi/(tenure*12);
			return"Hello "+name+"!\nLoan Amount: Rs."+amount+" Your EMI for the loan is: "+emi+" for "+tenure+" years\n\tMonthly EMI would be: "+memi+" \n\n Rate of Interest Applied: "+roi+"%";
	}
	
	@GetMapping("/deposit/{type}")
	public String fixedRecurring(@PathVariable String type, @RequestParam("amount") int amount, @RequestParam("duration") int duration)
	{
		int roi=0;
		double emi=0,maturity=0;
		if(amount<=0) {
			return "Invalid Amount Specified!";
		}
		if(amount<=0) {
			return "Invalid duartion Specified!";
		}
		if(duration==1) {
			roi=5;
		}
		else if(duration==2) {
			roi=6;
		}
		else if((duration>=3) && (duration<=5)) {
			roi=7;
		}
		else if((duration>=6) && (duration<=10)) {
			roi=6;
		}
		else {
			return "Invalid Duration Specified";
		}
		//double a=0,b=0;
		if(type.equalsIgnoreCase("rd")) {
			amount=amount*12;
			emi=(amount*duration*roi)/100;
			maturity=amount+emi;
			//amount=amount*12*duration;
		}
		if(type.equalsIgnoreCase("fd")) {
			emi=(amount*duration*roi)/100;
			maturity=amount+emi;
		}	
		
		if(type.equalsIgnoreCase("fd"))
			return "For Fixed Deposit, Your maturity amount after "+duration+" years is: "+maturity+"\n\tIn which, your Principal Amount is: Rs."+amount+"\n\tRs."+emi+" is the interest amount you get.\n\nAnd the Rate of Interest Applied is: "+roi+"%";
		else if(type.equalsIgnoreCase("rd")) {
			return "For Recurring Deposit, Your maturity amount after "+duration+" years is: "+maturity+"\n\tIn which, your Principal Amount is: Rs."+amount+"\n\tRs."+emi+" is the interest amount you get.\n\nAnd the Rate of Interest Applied is: "+roi+"%";
		}
		return "null";
	}
	
	
//	@GetMapping("/deposit/fd")
//	public String fixed(@RequestParam("amount") int amount, @RequestParam("duration") int duration)
//	{
//		int roi=0;
//		double emi,maturity;
//		if(amount<=0) {
//			return "Invalid Amount Specified!";
//		}
//		if(duration==1) {
//			roi=5;
//		}
//		else if(duration==2) {
//			roi=6;
//		}
//		else if((duration>=3) && (duration<=5)) {
//			roi=7;
//		}
//		else if((duration>=6) && (duration<=10)) {
//			roi=6;
//		}
//		else {
//			return "Invalid Duration Specified";
//		}
//		emi=(amount*duration*roi)/100;
//		maturity=amount+emi;
//		return "For Fixed Deposit, Your maturity amount after "+duration+" years is: "+maturity+"\n And the Rate of Interest Applied is: "+roi+"%";
//	}
//	
//	@GetMapping("/deposit/rd")
//	public String recurringDeposit(@RequestParam("amount") int amount, @RequestParam("duration") int duration)
//	{
//		int roi=0;
//		double emi,maturity;
//		if(amount<=0) {
//			return "Invalid Amount Specified!";
//		}
//		amount=amount*12;
//		if(duration==1) {
//			roi=5;
//		}
//		else if(duration==2) {
//			roi=6;
//		}
//		else if((duration>=3) && (duration<=5)) {
//			roi=7;
//		}
//		else if((duration>=6) && (duration<=10)) {
//			roi=6;
//		}
//		else {
//			return "Invalid Duration Specified";
//		}
//		emi=(amount*duration*roi)/100;
//		maturity=amount+emi;
//		return "For Recurring Deposit, Your maturity amount after "+duration+" years is: "+maturity+"\n And the Rate of Interest Applied is: "+roi+"%";
//	}
	
//	Account list1=(Account)repo.findByAccountno(fromAcct);
//	Account list2=(Account)repo.findByAccountno(toAcct);
//	if(list1.getActive().equals("yes") && list2.getActive().equals("yes"))
//	{
//		if (list2.getIfsccode() == ifsccode)
//		{
//			if (list1.getAmount() >= amt)
//			{
//				if (list1.getAmount() >= amt + 5000)
//				{
//					repo.amountTransaction(fromAcct, amt);
//					repo.amountReceive(toAcct, amt);
//					return "Transaction SUCCESSFUL!";
//				}
//				//else if(list1.getAmount() < amt+5000)
//				else
//				{
//					if(list1.getAmount()-200 >= 0) {
//						repo.minBalancePenalty(fromAcct, amt);
//						repo.amountReceive(toAcct, amt);
//						return "Transaction SUCCESSFUL!";
//					}
//					//else if (list1.getAmount()-200 < 0)
//					else {
//						repo.amountTransaction(fromAcct, amt);
//						repo.amountReceive(toAcct, amt);
//						return "Transaction SUCCESSFUL!";
//					}
//				}
//			}
//			else {
//				return "INSUFFICIENT BALANCE";
//			}
//		}
//		else {
//			return "IFSC Code didn't match";
//		}
//	}
//	else {
//		return "ACCOUNT INACTIVE";
//	}
//}
//else {
//	return "TRANSACTION UNSUCCESSFUL!";
//}
	@PostMapping("/account")
	public String addAccount(@RequestBody Account a) {
		if (repo.existsById(a.getAccountno())) {
			return "Account No already exists! ";
		} else {
			repo.save(a);
			return "Account Successfully Added!";
		}
	}

	@PutMapping("/account/{accountno}")
	public String updateAccount(@RequestBody Account a, @PathVariable int accountno) {
		if (repo.existsById(accountno)) {
			repo.save(a);
			return "Account Details Successfully Updated!";
		} else {
			return "Sorry! Invalid Account Number...";
		}
	}

	@DeleteMapping("/account/{accountno}")
	public String deactivateAccount(@PathVariable("accountno") int accountno) {
		if (repo.existsById(accountno)) {
			repo.disableAccount(accountno);
			return "Account Deactivated!";
		} else {
			return "Sorry! Invalid Account Number...";
		}
	}

	@GetMapping("/accounts/status")
	public List<Account> accountStatus(@RequestParam String active) {
		return repo.findByActive(active);
	}

	@GetMapping("/accounts/amount")
	public List<Account> accountAmount(@RequestParam int min, @RequestParam int max) {
		return repo.findByAmountBetweenOrderByAmount(min, max);
	}

	@GetMapping("/accounts/bank")
	public List<Account> bankName(@RequestParam("name") String bank) {
		return repo.getByBank(bank);
	}

//	@DeleteMapping("/account/{accountno}")
//	public void deactivateAccount(@PathVariable int accountno)
//	{
//		repo.disableAccount(accountno);
//	}

	@GetMapping("/accounts/type/{type}")
	public List<Account> getByType(@PathVariable String type) {
		return repo.getByType(type);
	}

}
