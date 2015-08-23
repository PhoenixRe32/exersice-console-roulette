package com.gamesys.consoleroulette.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gamesys.consoleroulette.application.ConsoleRoulette;
import com.gamesys.consoleroulette.application.bet.Player;

public class ConsoleRouletteTest
{

	// private String valid1 = "Tiki_Monkey,1.0,1.0\nBarbara,2.0,1.0\n";
	// private String valid2 = "Tiki_Monkey,,1.0\nBarbara,2.0,1.0\n";
	// private String valid3 = "Tiki_Monkey,,1.0\nBarbara,2.0\n";
	// private String valid4 = "Tiki_Monkey\nBarbara,\n";
	// private String invalid1 = "Tiki_Monkey,-0.2\n";
	// private String invalid2 = "Tiki_Monkey,money\n";
	// private String invalid3 = "Tiki_Monkey,0.5,12.52,\n";
	// private String invalid4 = "\n";

	private String valid[] =
	{ "Tiki_Monkey,1.0,1.0\nBarbara,2.0,1.0\n", "Tiki_Monkey,,1.0\nBarbara,2.0,1.0\n",
			"Tiki_Monkey,,1.0\nBarbara,2.0\n", "Tiki_Monkey\nBarbara,\n" };

	private String invalid[] =
	{ "Tiki_Monkey,-0.2\n", "Tiki_Monkey,money\n", "Tiki_Monkey,0.5,12.52,\n", "\n" };

	private final int MAX_TOKENS = 3;

	private ConsoleRoulette cr;

	@BeforeClass
	public static void oneTimeSetUp()
	{
		// one-time initialization code
		System.out.println("@BeforeClass - oneTimeSetUp");
	}

	@AfterClass
	public static void oneTimeTearDown()
	{
		// one-time cleanup code
		System.out.println("@AfterClass - oneTimeTearDown");
	}

	@Before
	public void setUp()
	{
		cr = new ConsoleRoulette();
		System.out.println("@Before - setUp");
	}

	@After
	public void tearDown()
	{
		System.out.println("@After - tearDown");
	}

	@Test
	public void testPlayerValidDataInput() throws IOException
	{
		String in;
		
		System.out.println("@Test: Player valid data input");
		
		for (int i = 0; i < valid.length; i++)
		{
			in = valid[i];
			String msg = "The number of players created is not 2 for input: " + i; 
			StringReader sr = new StringReader(in);
			BufferedReader br = new BufferedReader(sr);
			Map<String, Player> players = cr.createPlayerMap(br, MAX_TOKENS);
			assertEquals(msg, 2, players.size());
			
			for (Player player : players.values())
			{
				System.out.println(player.toString());
			}
			System.out.println("--------------------");
		}
	}
	
	@Test
	public void testPlayerInvalidDataInput() throws IOException
	{
		String in;
		
		System.out.println("@Test: Player invalid data input");
		
		for (int i = 0; i < invalid.length; i++)
		{
			in = invalid[i];
			String msg = "The number of players created is not 0 for input: " + i; 
			StringReader sr = new StringReader(in);
			BufferedReader br = new BufferedReader(sr);
			Map<String, Player> players = cr.createPlayerMap(br, MAX_TOKENS);
			assertEquals(msg, 0, players.size());
			
			for (Player player : players.values())
			{
				System.out.println(player.toString());
			}
			System.out.println("--------------------");
		}
	}
}