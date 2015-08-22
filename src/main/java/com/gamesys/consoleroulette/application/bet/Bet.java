package com.gamesys.consoleroulette.application.bet;

import java.math.BigDecimal;

/**
 * Class representing a bet.
 * 
 * @author Andreas
 *
 */
public class Bet
{
	// Number to bet on.
	private String number;
	
	// Amount to bet.
	private BigDecimal amount;
	
	public Bet(String number, BigDecimal amount)
	{
		this.number = number;
		this.amount = amount;
	}
	
	/**
	 * Return the number the player bet on.
	 * 
	 * @return number
	 */
	public String getNumber()
	{
		return number;
	}

	/**
	 * Return the amount the player bet.
	 * 
	 * @return amount
	 */
	public BigDecimal getAmount()
	{
		return amount;
	}
}
