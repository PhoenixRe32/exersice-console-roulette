package com.gamesys.consoleroulette.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gamesys.consoleroulette.application.bet.Bet;
import com.gamesys.consoleroulette.application.bet.Player;
import com.gamesys.consoleroulette.application.game.GameBetMonitor;
import com.gamesys.consoleroulette.application.game.GameNumberGenerator;

public class GameBetMonitorTest
{
	private static final int NUM_TOKENS = 3;

	private static final String BET_DELIMITER = " ";

	private static final int LOWER_BOUND = 1;

	private static final int UPPER_BOUND = 36;

	private GameBetMonitor gbm;

	private ConcurrentHashMap<String, Player> players;

	private GameNumberGenerator gng;

	private String valid[] =
	{ "Barbara 1 1.60\n", "Barbara 1 1.60\nTiki_Monkey 2 3.20\n" };

	private String invalid[] =
	{ "Tiki_Monkey -0.2\n", "Tiki_Monkey 0.2 34\n", "Tiki_Monkey,0.5\n", "Andreas 0.5\n" };

	@BeforeClass
	public static void oneTimeSetUp()
	{
	}

	@AfterClass
	public static void oneTimeTearDown()
	{
	}

	@Before
	public void setUp()
	{
		System.out.println("@Before - setUp");

		players = new ConcurrentHashMap<String, Player>(2);
		players.put("Barbara", new Player("Barbara"));
		players.put("Tiki_Monkey", new Player("Tiki_Monkey", new BigDecimal(0), new BigDecimal(0)));

		gng = EasyMock.createMock(GameNumberGenerator.class);

		gbm = new GameBetMonitor(LOWER_BOUND, UPPER_BOUND, players, gng);
	}

	@After
	public void tearDown()
	{
	}

	@Test
	public void testBetCreationValidDataInput() throws IOException
	{
		String in;

		System.out.println("@Test: Bet creation with valid data input");

		for (int i = 0; i < 1; i++)
		{
			in = valid[i];

			String msg = "The total bet is not as expected for [" + in + "]";

			StringReader sr = new StringReader(in);
			BufferedReader br = new BufferedReader(sr);
			Pattern pattern = Pattern.compile(BET_DELIMITER);

			Bet bet = gbm.createBet(br, pattern, NUM_TOKENS);

			assertEquals(msg, bet.getAmount(), new BigDecimal("1.60"));
		}
	}

	@Test
	public void testBetCreationInvalidDataInput() throws IOException
	{
		try
		{
			String in;
			
			System.out.println("@Test: Bet creation with valid data input");
			
			for (int i = 0; i < invalid.length; i++)
			{
				in = invalid[i];
				
				String msg = "The total bet is not as expected for [" + in + "]";
				
				StringReader sr = new StringReader(in);
				BufferedReader br = new BufferedReader(sr);
				Pattern pattern = Pattern.compile(BET_DELIMITER);
				
				gbm.createBet(br, pattern, NUM_TOKENS);
				
				fail(msg);
			}
		}
		catch (IllegalArgumentException iae)
		{
		}
	}

	@Test
	public void testBetRecording()
	{
		System.out.println("@Test: Bet recording");
		
		Bet bet = new Bet("Barbara", "6", new BigDecimal(2));
		
		EasyMock.expect(gng.getCurrentGameId()).andStubReturn(10l);
		EasyMock.replay(gng);
		
		String msg = "The total bet is not as expected for [" + bet.toString() + "]";
		
		gbm.recordBet(bet);
		
		assertEquals(msg, bet.getAmount(), players.get(bet.getUserName()).getTotalBet());
		
		assertTrue(players.get(bet.getUserName()).getBetHistory().containsKey(new Long(10)));
	}
}