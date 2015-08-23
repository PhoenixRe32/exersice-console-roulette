package com.gamesys.consoleroulette.application.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamesys.consoleroulette.application.bet.Bet;
import com.gamesys.consoleroulette.application.bet.Player;
import com.gamesys.consoleroulette.application.bet.RouletteRange;

/**
 * Class that monitors all the betting. Has a thread that accepts lines of input for every bet. Processes that line and
 * updates accordingly the bet history of every player.
 * 
 * @author Andreas
 *
 */
public class GameBetMonitor extends Game implements Runnable
{
	// logger, because system.out and system.err... meh
	private static final Logger log = LoggerFactory.getLogger(GameBetMonitor.class);

	// Number of tokens an input line should be divided into.
	private final int NUM_TOKENS = 3;

	// A map of all the player participating in the game.
	private ConcurrentHashMap<String, Player> players;

	private GameNumberGenerator game;

	public GameBetMonitor(int lowerBound, int upperBound, ConcurrentHashMap<String, Player> players,
			GameNumberGenerator game)
	{
		super(lowerBound, upperBound);
		this.players = players;
		this.game = game;
	}

	@Override
	public void run()
	{
		// Reader for the input from the console. We will be reading lines of input.
		BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

		// The regular expression to use to split every line of input. Might save a bit time on the long run.
		Pattern pattern = Pattern.compile(" ");

		/*
		 * Read a line of input from the console. readLine() is blocking so it will read everything on the console until
		 * the next \n character (when the user presses Enter). Will then continue to process the line and loop all over
		 * again. TODO: Infinite loop. see if I can use a key combination to terminate it.
		 */
		while (true)
		{
			Bet bet;
			try
			{
				bet = createBet(consoleReader, pattern, NUM_TOKENS);
			}
			catch (IOException e)
			{
				log.error("There was a problem with reading the input.");
				continue; // no point continuing, read the next input.
			}
			catch (NumberFormatException nfe)
			{
				log.error("The roulette number must be a natural number (i.e. 2, 23) and "
						+ "the bet amount must be a real number (i.e. 0.15, 3.50)");
				continue; // no point continuing, read the next input.
			}
			catch (IllegalArgumentException iae)
			{
				log.error(iae.getMessage());
				continue; // no point continuing, read the next input.
			}

			recordBet(bet);
		}

	}

	private Bet createBet(final BufferedReader consoleReader, final Pattern pattern, final int NUM_TOKENS)
			throws IOException, IllegalArgumentException
	{
		// String to read the input from the console.
		String line = null;
		// Array of strings to store the split input line. For our example the correct input should be 3 tokens
		// separated by a space in the format "Username Number_to_bet_on Bet_Amount".
		String[] betDetails;

		// Variables to be used while processing the bet and to use when creating the bet object.
		String userName;
		String rouletteChoice;
		BigDecimal betAmount;

		/*
		 * Read line from console. Split line in the distinct tokens signified by the split pattern. We defined
		 * beforehand how many tokens we expect so we limit the split to so many tokens.
		 */
		line = consoleReader.readLine();
		betDetails = pattern.split(line, NUM_TOKENS + 1);

		/*
		 * While we limit the number of tokens to the expected number we also check if the number of tokens is different
		 * than expected and print an error message to the user so he can correct his input.
		 */
		if (betDetails.length != NUM_TOKENS)
		{
			throw new IllegalArgumentException("'" + line + "' was malformed. The format is 'Username Number Bet'.");
		}

		/*
		 * Here we process the distinct tokens of the input line. We read the first as a String, and convert the
		 * following two to an integer and a BigDecimal respectively assuming the second will be the number on which the
		 * player bet and the third the amount he bet. For each token we also do a little validation; valid numbers,
		 * existing names e.t.c.
		 */
		userName = betDetails[0];
		if (!nameIsValid(userName))
		{
			throw new IllegalArgumentException("The userName does not exist in record. Please check your spelling.");
		}

		rouletteChoice = betDetails[1].toUpperCase();
		if (!betIsValid(rouletteChoice))
		{
			throw new IllegalArgumentException(
					"The roulette number must be a number in the range 1-36 inclusive, ODD or EVEN.");
		}

		betAmount = new BigDecimal(betDetails[2]);
		if (!betAmountIsValid(betAmount))
		{
			throw new IllegalArgumentException(
					"The bet amount must be a positive number with at most 2 decimal places.");
		}

		// TODO perhaps it would be better to use smallest denomination, i.e pence, cents, etc
		return new Bet(userName, rouletteChoice, betAmount);
	}

	/**
	 * In my case, since I have one thread for the monitoring of bets (a thread where the input of bets is feasible) and
	 * one thread for the game itself (generating the number that won and the evaluation of the results) one can make a
	 * bet while the game is running. What I do is that I keep a map of all the bets of each player which is indexed on
	 * an integer which act as an id for each game (30s segment). This id is managed by the thread running the game and
	 * each 30 seconds I generate a winning number and increase the game id (since generating a number means the current
	 * game ended and a new one will start once the evaluation of the bets completes). The bet monitor thread has a
	 * reference to the game thread and can access a method to get the value if the id. (I should point out that the id
	 * is an AtomicLong and is only modified by the game thread and only run by the bet monitor thread. In an expansion
	 * it could be read by multiple threads.) So when a bet is made, the bet monitor gets the game id and checks if the
	 * player has made a bet already in which case he rejects it based on my assumption I stated above. If he hasn't
	 * then the bet monitor enters another block of code where a bet object is created and then inserted in the bet
	 * history map of the player. Of course it could be that by that time the game id has changed but since it is going
	 * to be a new game, then there isn't a problem (programmatically speaking) since the new game also means that the
	 * player hasn't made a bet and this is a valid action and what would happen in the case where a change in the id
	 * didn't happen (the id has already changed). However it is not as clear as I would like. And is definitely as
	 * clear if one would assume knowledge of a player in which game he is betting on.
	 */
	private void recordBet(Bet bet)
	{
		Player player = players.get(bet.getUserName());
		ConcurrentHashMap<Long, Bet> betHistory = player.getBetHistory();
		if (!betHistory.containsKey(game.getCurrentGameId()))
		{
			betHistory.put(game.getCurrentGameId(), bet);
			System.out.println("OK! Bet accepted.");
			player.updateTotalBet(bet.getAmount());
		}
		else
		{
			System.out.println("Bet rejected. A bet from this player was already made. It cannot be changed.");
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
	 * Validates the bet. Checks that the number the bet was made on is in the map of RouletteRange. It does that by
	 * checking if a key with the value of the argument is present in the map.
	 * 
	 * @param rouletteChoice
	 * @return the multiplier for the choice. Otherwise it returns false.
	 */
	private boolean betIsValid(String rouletteChoice)
	{
		return RouletteRange.valueExists(rouletteChoice);
	}

	/**
	 * Validates the bet amount. Checks that the amount is a positive number and below the maximum bet. TODO: check
	 * decimal places as well, up to 2. Anything more doesn't make sense.
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
}
