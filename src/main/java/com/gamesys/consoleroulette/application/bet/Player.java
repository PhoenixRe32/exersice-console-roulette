package com.gamesys.consoleroulette.application.bet;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for representing a player of a game.
 * 
 * @author Andreas
 *
 */
public class Player implements BetListener
{
	private String userName;

	volatile private BigDecimal totalBet;

	volatile private BigDecimal totalWin;

	private ConcurrentHashMap<Long, Bet> betHistory;

	// This is only going to be accessed from its owner... for now.
	private Map<Long, BetResult> betResultHistory;

	public Player(String userName, BigDecimal totalBet, BigDecimal totalWin)
	{
		this.userName = userName;
		this.totalBet = totalBet;
		this.totalWin = totalWin;
		betHistory = new ConcurrentHashMap<Long, Bet>();
		betResultHistory = new HashMap<Long, BetResult>();
	}

	public Player(String userName)
	{
		// this(userName, 0.0d, 0.0d);
		this.userName = userName;
		this.totalBet = new BigDecimal(0);
		this.totalWin = new BigDecimal(0);
		betHistory = new ConcurrentHashMap<Long, Bet>();
		betResultHistory = new HashMap<Long, BetResult>();
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

	@Override
	public void betCompleted(long gameId, int winningNumber)
	{
		Bet bet = betHistory.get(gameId);

		BetResult betResult = evaluateResults(gameId, winningNumber, bet);

		if (betResult != null)
		{
			// "Player", "Bet", "Outcome", "Winnings", "TotalBet", "TotalWin"
			System.out.printf("%-10s %5s %9s %10s %10s %10s %n", userName, bet.getChoice(), betResult.getOutcome(),
					betResult.getWinnings(), totalBet, totalWin);
		}
	}

	/**
	 * Evaluates the bets against the winning number.
	 * 
	 * @param winningNumber
	 * @param gameId
	 * @return a result that contain information used to present the results.
	 */
	private BetResult evaluateResults(long gameId, int winningNumber, Bet bet)
	{
		/*
		 * We will access all his bets and then evaluate them against the result of this game. When evaluating them we
		 * will also prepare the object to represent the result that is also to be used in the presentation.
		 */
		Bet currentBet = betHistory.get(gameId);
		if (currentBet == null)
		{
			// This player didn't place a bet in this game, moving on.
			return null;
		}

		// Outcome of bet.
		Outcome outcome;

		// Winnings of bet.
		// TODO perhaps it would be better to use smallest denomination, i.e pence, cents, etc
		BigDecimal winnings;

		String betChoice = currentBet.getChoice();
		BigDecimal betAmount = currentBet.getAmount();

		if (RouletteRange.ODD.getValue().equals(betChoice))
		{
			if ((winningNumber % 2) == 1)
			{
				outcome = Outcome.WIN;
				winnings = betAmount.multiply(RouletteRange.ODD.getMultiplier());
			}
			else
			{
				outcome = Outcome.LOSE;
				winnings = BigDecimal.ZERO;
			}
		}
		else if (RouletteRange.EVEN.getValue().equals(betChoice))
		{
			if ((winningNumber % 2) == 0)
			{
				outcome = Outcome.WIN;
				winnings = betAmount.multiply(RouletteRange.EVEN.getMultiplier());
			}
			else
			{
				outcome = Outcome.LOSE;
				winnings = BigDecimal.ZERO;
			}
		}
		else
		{
			String winningNumberStr = Integer.toString(winningNumber);
			if (winningNumberStr.equals(betChoice))
			{
				outcome = Outcome.WIN;
				winnings = betAmount.multiply(RouletteRange.getMultiplierByValue(betChoice));
			}
			else
			{
				outcome = Outcome.LOSE;
				winnings = BigDecimal.ZERO;
			}

		}

		// Create result object and add it to the history.
		BetResult betResult = new BetResult(winningNumber, betChoice, winnings, userName, outcome);
		betResultHistory.put(gameId, betResult);

		// Update total win of player
		updateTotalWin(winnings);

		return betResult;
	}
	
	@Override
	public String toString()
	{
		return "User Name: " + userName + "\nTotal Bet: " + totalBet + "\nTotal Win: " + totalWin;
		
	}
}
