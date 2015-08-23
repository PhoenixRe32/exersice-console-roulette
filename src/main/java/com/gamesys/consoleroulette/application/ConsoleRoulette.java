package com.gamesys.consoleroulette.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamesys.consoleroulette.application.bet.Player;
import com.gamesys.consoleroulette.application.game.GameBetMonitor;
import com.gamesys.consoleroulette.application.game.GameNumberGenerator;

/**
 * Class that contains entry point. Sets up the threads, reads the file with the player details and starts the main
 * application/game.
 * 
 * @author Andreas
 *
 */
public final class ConsoleRoulette
{
	// logger, because system.out and system.err... meh
	private static final Logger log = LoggerFactory.getLogger(ConsoleRoulette.class);

	private final String PLAYER_RECORD = "player_data.txt";

	private final int LOWER_BOUND = 1;

	private final int UPPER_BOUND = 36;

	private final int FRQUENCY = 30;

	private final int MAX_TOKENS = 3;

	public static void main(String[] args)
	{
		ConsoleRoulette cr = new ConsoleRoulette();

		ConcurrentHashMap<String, Player> players = null;

		try
		{
			// Getting the file that contains the names of the players.
			InputStream inputStream = cr.getClass().getClassLoader().getResourceAsStream(cr.PLAYER_RECORD);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			// Create the player map.
			players = cr.createPlayerMap(bufferedReader, cr.MAX_TOKENS);

			bufferedReader.close();
		}
		catch (IOException e)
		{
			log.error("There was a problem with reading the input.");
			return;
		}

		// If the file was empty then exit. No game can be played.
		// SHould never really be null. It would be caught by the exception.
		if (players.isEmpty())
		{
			log.info("The file was empty or did not have any valid entries. Exiting...");
			return;
		}

		GameNumberGenerator gameNumberGenerator = new GameNumberGenerator(cr.LOWER_BOUND, cr.UPPER_BOUND, cr.FRQUENCY);
		// Adding listeners for players on the game number generator so i can notify them when the bet is complete.
		for (Map.Entry<String, Player> playerEntry : players.entrySet())
		{
			gameNumberGenerator.addBetListener(playerEntry.getValue());
		}

		// Setting the players in the bet monitor.
		GameBetMonitor betMonitor = new GameBetMonitor(cr.LOWER_BOUND, cr.UPPER_BOUND, players, gameNumberGenerator);

		System.out.println("************************\n" 
						+  "*                      *\n" 
						+  "* Starting roulette... *\n"
						+  "*                      *\n" 
						+  "************************");

		// The thread running the two processes.
		Thread gameThread = new Thread(gameNumberGenerator, "GameThread");
		Thread betMonitorThread = new Thread(betMonitor, "BetMonitor");

		gameThread.start();
		betMonitorThread.start();
	}

	public ConcurrentHashMap<String, Player> createPlayerMap(BufferedReader bufferedReader, final int MAX_TOKENS)
			throws IOException
	{
		ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<String, Player>(2);

		// Reading the file and creating the players.
		String line;
		String[] playerDetails;
		Pattern pattern = Pattern.compile(",");

		// Variables for the details of the player
		String userName = null;
		BigDecimal totalWin = BigDecimal.ZERO;
		BigDecimal totalBet = BigDecimal.ZERO;

		while ((line = bufferedReader.readLine()) != null)
		{
			playerDetails = pattern.split(line, MAX_TOKENS + 1);

			/*
			 * While we limit the number of tokens to the expected number we also check if the tokens are less and print
			 * an error message to the user so he can correct his input.
			 */
			if (playerDetails.length > MAX_TOKENS)
			{
				log.warn("'" + line + "' was malformed. The format is 'Username [TotalWin] [TotalBet]'.");
				continue; // no point continuing, read the next input.
			}

			// The user name will always be present but it might be empty.
			if (playerDetails[0].isEmpty())
			{
				log.warn("The user name cannot be empty.");
				continue; // no point continuing, read the next input.
			}
			userName = playerDetails[0];

			try
			{
				if (playerDetails.length == 3)
				{
					totalWin = (playerDetails[1].isEmpty()) ? BigDecimal.ZERO : new BigDecimal(playerDetails[1]);
					if (totalWin.compareTo(BigDecimal.ZERO) == -1 )
					{
						log.warn("The total win amount must be positive.");
						continue; // no point continuing, read the next input.
					}
					
					totalBet = (playerDetails[2].isEmpty()) ? BigDecimal.ZERO : new BigDecimal(playerDetails[2]);
					if (totalBet.compareTo(BigDecimal.ZERO) == -1 )
					{
						log.warn("The total bet amount must be positive.");
						continue; // no point continuing, read the next input.
					}
				}
				else if (playerDetails.length == 2)
				{
					totalWin = (playerDetails[1].isEmpty()) ? BigDecimal.ZERO : new BigDecimal(playerDetails[1]);
					if (totalWin.compareTo(BigDecimal.ZERO) == -1 )
					{
						log.warn("The total win amount must be positive.");
						continue; // no point continuing, read the next input.
					}
				}
			}
			catch (NumberFormatException nfe)
			{
				log.error("The total bet and total win amount must be a real numbers (i.e. 0.15, 3.50).");
				continue; // no point continuing, read the next input.
			}

			players.put(userName, new Player(userName, totalBet, totalWin));
		}

		return players;
	}

}
