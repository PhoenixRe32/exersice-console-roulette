package com.gamesys.consoleroulette.application;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for representing a player of a game.
 * 
 * @author Andreas
 *
 */
public class Player
{
	private String userName;
	
	volatile private BigDecimal totalBet;
	
	volatile private BigDecimal totalWin;
	
	private ConcurrentHashMap<Long, Bet> betHistory;
	
	public Player(String userName, BigDecimal totalBet, BigDecimal totalWin)
	{
		this.userName = userName;
		this.totalBet = totalBet;
		this.totalWin = totalWin;
		betHistory = new ConcurrentHashMap<Long, Bet>();
	}
	
	public Player(String userName)
	{
		// this(userName, 0.0d, 0.0d);
		this.userName = userName;
		this.totalBet = new BigDecimal(0);
		this.totalWin = new BigDecimal(0);
		betHistory = new ConcurrentHashMap<Long, Bet>();
	}
	
	/**
	 * Return the bet of the user for the game.
	 */
	public ConcurrentHashMap<Long, Bet> getBetHistory()
	{
		return betHistory;
	}

	/**
	 * Gets the user name of the player.
	 * 
	 * @return userName
	 */
	public String getUserName()
	{
		return userName;
	}

	public void updateTotalBet(BigDecimal betAmount)
	{
		totalBet = totalBet.add(betAmount);
	}

	public void updateTotalWin(BigDecimal winnings)
	{
		totalWin = totalWin.add(winnings);
	}

	public BigDecimal getTotalBet()
	{
		return totalBet;
	}

	public BigDecimal getTotalWin()
	{
		return totalWin;
	}
}
