package com.gamesys.consoleroulette.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Class that monitors all the betting. Has a thread that accepts lines of input for every bet. Processes that line
 * and updates accordingly the bet history of every player.
 * 
 * @author Andreas
 *
 */
public class GameBetMonitor extends Game implements Runnable
{
	// A map of all the player participating in the game.
	private ConcurrentHashMap<String, Player> players;

	// Flag to indicate when a game ended and the bets are being settled.
	volatile private boolean settlingBets;
	
	private volatile Long gameId;

	public GameBetMonitor(int lowerBound, int upperBound, ConcurrentHashMap<String, Player> players)
	{
		super(lowerBound, upperBound);
		this.players = players;
		gameId = 0l;
		settlingBets = false;
	}

	@Override
	public void run()
	{
		// Reader for the input from the console. We will be reading lines of input.
		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

		// String to read the input from the console.
		String line = null;
		// Array of strings to store the split input line. For our example the correct input should be 3 tokens
		// separated by
		// a space in the format "Username Number_to_bet_on Bet_Amount".
		int tokens = 3;
		String[] betDetails;
		// The regular expression to use to split every line of input. Might save a bit time on the long run.
		Pattern pattern = Pattern.compile(" ");

		// Variables to be used in the while loop when processing the bet.
		String userName;
		int rouletteNumber;
		BigDecimal betAmount;

		/*
		 * Read a line of input from the console. readLine() is blocking so it will read everything on the console until
		 * the next \n character (when the user presses Enter). Will then continue to process the line and loop all over
		 * again. TODO: Infinite loop. see if I can use a key combination to terminate it.
		 */
		while (true)
		{
			try
			{
				/*
				 * Read line from console. Split line in the distinct tokens signified by the split pattern. We defined
				 * beforehand how many tokens we expect so we limit the split to so many tokens.
				 */
				line = consoleReader.readLine();
				betDetails = pattern.split(line, tokens);

				/*
				 * While we limit the number of tokens to the expected number we also check if the tokens are less and
				 * print an error message to the user so he can correct his input.
				 */
				if (betDetails.length < tokens)
				{
					System.out.println("The input was malformed. The format expected is 'Username Number Bet'.");
					continue; // no point continuing, read the next input.
				}

				/*
				 * Here we process the distinct tokens of the input line. We read the first as a String, and convert the
				 * following two to an integer and a BigDecimal respectively assuming the second will be the number on
				 * which the player bet and the third the amount he bet. For each token we also do a little validation;
				 * valid numbers, existing names e.t.c.
				 */
				userName = betDetails[0];
				if (!nameIsValid(userName))
				{
					System.out.println("The userName does not exist in record. Please check your spelling.");
					continue; // no point continuing, read the next input.
				}
				
				rouletteNumber = Integer.valueOf(betDetails[1]);
				if (!betIsValid(rouletteNumber))
				{
					System.out.println("The roulette number must be a number in the range 1-36 inclusive.");
					continue; // no point continuing, read the next input.
				}

				betAmount = new BigDecimal(betDetails[2]);
				if (!betAmountIsValid(betAmount))
				{
					System.out.println("The bet amount must be a positive number with at most 2 decimal places.");
					continue; // no point continuing, read the next input.
				}
			}
			catch (IOException e)
			{
				System.err.println("There was a problem with reading the input.");
				e.printStackTrace();
				continue; // no point continuing, read the next input.
			}
			catch (NumberFormatException nfe)
			{
				System.err.println(nfe.getMessage());
				System.err.println("The roulette number must be a natural number (i.e. 2, 23) and "
						+ "the bet amount must be a real number (i.e. 0.15, 3.50)");
				continue; // no point continuing, read the next input.
			}

			/*
			 * After the line was processed and everything was fine we will process the player which made the bet. If
			 * bets are being settled then we will wait for a bit. We will add his bet details to the map storing his
			 * bets for every game. I am going to assume that if a bet was already made in the past and is made
			 * again, the new bet will rejected. Before doing anything we will check that a game is in progress.
			 */
			waitIfSettlingBets();
			
			recordBet(userName, rouletteNumber, betAmount);			
		}

	}
	
	void recordBet(String userName, int rouletteNumber, BigDecimal betAmount)
	{
		Player player = players.get(userName);
		ConcurrentHashMap<Long, Bet> betHistory = player.getBetHistory();
		if (!betHistory.containsKey(gameId))
		{
			// TODO perhaps it would be better to use smallest denomination, i.e pence, cents, etc
			Bet bet = new Bet(rouletteNumber, betAmount);
			betHistory.put(gameId, bet);
			System.out.println("OK! Bet accepted.");
			player.updateTotalBet(betAmount);
		}
		else
		{
			System.out.println("Bet rejected. A bet from this player was already made. It cannot be changed.");
		}
	}
	
	void waitIfSettlingBets() 
	{
		int retries = 0;
		while (settlingBets)
		{
			try
			{
				System.out.println("A game has just ended and the bets are being settled. Don't go anywhere. "
						+ "In just a moment a new game will start!!!");
				++retries;
				if (retries > 30)
				{
					System.err
							.println("There has been a technical problem. We apologise for this. "
									+ "Do not worry, your bets will be returned to you. "
									+ "We hope this does not discourage you to continue seeking entertainment with Gamesys.");
					System.exit(-1);
				}

				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Validates the user. Checks that the userName is in the table as a key.
	 * 
	 * @param userName
	 * @return true if the userName exists. Otherwise it returns false.
	 */
	private boolean nameIsValid(String userName)
	{
		return players.containsKey(userName);
	}
	
	
	/**
	 * Validates the bet. Checks that the number the bet was made on is in the range 1-36 inclusive.
	 * TODO: check decimal places as well, up to 2. Anything more doesn't make sense.
	 * 
	 * @param rouletteNumber
	 * @return true if the number is valid. Otherwise it returns false.
	 */
	private boolean betIsValid(int rouletteNumber)
	{
		return (rouletteNumber >= 1 && rouletteNumber <= 36);
	}

	/**
	 * Validates the bet amount. Checks that the amount is a positive number and below the maximum bet.
	 * 
	 * @param betAmount
	 * @return true if the amount is valid. Otherwise it returns false.
	 */
	private boolean betAmountIsValid(BigDecimal betAmount)
	{
		return (!BigDecimal.ZERO.equals(betAmount) && MAX_BET.compareTo(betAmount) == 1);
	}

	/**
	 * Returns a map of all the players to be monitored
	 * 
	 * @return the players
	 */
	public ConcurrentHashMap<String, Player> getPlayers()
	{
		return players;
	}

	// /**
	// * Returns true if the game number generator is settling the bets of the game. Otherwise false. The majority of
	// time
	// * this should be false. It should be set to true only for a very small time when the evaluation of results
	// happens.
	// *
	// * @return the settlingBets
	// */
	// public boolean isSettlingBets()
	// {
	// return settlingBets;
	// }

	/**
	 * If it's time to settle the bets - the game ended - then it is set to true. Once that evaluation is done, it
	 * should be set to false again for the next game.
	 * 
	 * @param settlingBets
	 *            the settlingBets to set
	 */
	public void setSettlingBets(boolean settlingBets)
	{
		this.settlingBets = settlingBets;
	}

	/**
	 * Sets the game id of the current game.
	 * 
	 * @param gameId the gameId to set
	 */
	public void setGameId(Long gameId)
	{
		this.gameId = gameId;
	}
	
	/**
	 * @return the game id of the current game.
	 */
	public Long getGameId()
	{
		return gameId;
	}
}
