package com.github.JHXSMatthew.Game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.entity.FallingBlock;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.github.JHXSMatthew.Core;
import com.github.JHXSMatthew.Config.Message;
import com.github.JHXSMatthew.Util.ActionBarUtil;
import com.github.JHXSMatthew.Util.EntityUtil;
import com.github.JHXSMatthew.Util.FireWorkUlt;
import com.github.JHXSMatthew.Util.LocationUtil;

public class Game {

	private static double PeopleCouldStart = Config.START_PERCENTAGE;
	private static int MaxPeopleCount = Config.MAX_PLAYERS;
	private static int startCounting = Config.START_COUNTDOWN;
	
	private static double shrinkTime = Config.BORDER_SHRINK_DURATION;
	
	private static boolean debug = false;
	
	private List<GameTeam> teams = Collections.synchronizedList(new ArrayList<GameTeam>());
	private List<GamePlayer> players = Collections.synchronizedList(new ArrayList<GamePlayer>());
	private List<Location> chestLocations = Collections.synchronizedList(new ArrayList<Location>());
	private WorldBorder border  = null;
	private GameState gameState = null;
	DecimalFormat df = new DecimalFormat("0.00");
	
	private BukkitTask currentTask = null;
	private BukkitTask chestTask = null;
	
	private boolean shouldUpdateDetail = true;
	
	public int publicCount = 0;
	private int priviousBorderScore = (int) Core.boarderSize;
	//private int currentBorderScore = 1000;
	
	
	
	public Game(){
		if(debug){
			changeState(GameState.Lobby);
			startCounting = 10;
			PeopleCouldStart = 2;
		}else{
			changeState(GameState.Generating);
		}
	}
	
	public void teleportFirst(GamePlayer gp){
		for(GamePlayer temp : players){
			if(!temp.isSpec()){
				gp.get().teleport(temp.get());
				break;
			}
		}
	}
	
	private GamePlayer getFirstPlayer(){
		for(GamePlayer gp: players){
			if(gp.isSpec()){
				continue;
			}
			return gp;
		}
		
		return null;
	}
	
	public void teamPlayer(GamePlayer a, GamePlayer b){
		if(a.isInTeam() && b.isInTeam()){
			a.sendMessage(ChatColor.YELLOW + " 您已经在一个队伍里了！ 请您使用物品栏第 8 格物品退出队伍后再组队.");
			return;
		}
		
		if(b.isInRequestList(a)){
			
			GamePlayer whoHasTeam = null;
			if(a.getTeam() != null){
				whoHasTeam = a;
			}
			if(b.getTeam() != null){
				whoHasTeam = b;
			}
			
			
			if(whoHasTeam == null){
				GameTeam gt = new GameTeam();
				gt.JoinTeam(a, true);
				gt.JoinTeam(b, true);
				teams.add(gt);
				
			}else{
				GameTeam gt = whoHasTeam.getTeam();
				gt.JoinTeam( gt.isInTeam(a)?b:a , true);
			}
			b.removeFromRequestList(a);
			shouldUpdateDetail = true;
			updateScoreboard(false);
		}else{
			a.addRequestList(b);
		}
		
		/*
		if(a.isInTeam() ){
			if(b.isInTeam()){
				//dead . 
			}else{
				if(b.isInRequestList(a)){
					b.removeFromRequestList(a);
					a.getTeam().JoinTeam(b, true);
				}else{
					a.addRequestList(b);
				}
			}
		}else{
			if(b.isInTeam()){
				if(a.isInRequestList(b)){
					a.removeFromRequestList(b);
					b.getTeam().JoinTeam(a, true);
				}else{
					b.addRequestList(a);
				}
			}else{
				
			}
		}
		
		*/
		/*
		GamePlayer whoHasTeam = null;
		if(a.getTeam() != null){
			whoHasTeam = a;
		}
		if(b.getTeam() != null){
			whoHasTeam = b;
		}
		
		if(whoHasTeam == null){
			GameTeam gt = new GameTeam();
			gt.JoinTeam(a, true);
			gt.JoinTeam(b, true);
			teams.add(gt);
			
		}else{
			GameTeam gt = whoHasTeam.getTeam();
			gt.JoinTeam( gt.isInTeam(a)?b:a , true);
		}
		
		shouldUpdateDetail = true;
		updateScoreboard();
		*/
	}
	
	
	
	
	public void GameJoin(GamePlayer gp){
		players.add(gp);
		if(gameState == GameState.Generating || gameState == GameState.Lobby || gameState == GameState.Starting){
			gp.setGameJoin();
			
			
			
			this.sendToAllMessage(ChatColor.YELLOW + " 玩家 " + gp.get().getName() + " 加入了游戏.  " + ChatColor.GREEN + "(" + players.size() + "/" + MaxPeopleCount + ") ");
			if(canBegin()){
				/*
				gameState = GameState.Starting;
				runTask(GameState.Starting);
				*/
				changeState(GameState.Starting);
			
			}
			
			shouldUpdateDetail = true;
			updateScoreboard(false);
			
		}else{
			gp.setGameSpec();
			gp.get().teleport(players.get(0).get());
			updateOneScoreboard(gp);
			
			//hide players
			for(GamePlayer temp : players){
				if(!temp.isSpec()){
					continue;
				}
				if(temp != gp){
					if(gp.get().canSee(temp.get()))
						gp.get().hidePlayer(temp.get());
					
					if(temp.get().canSee(gp.get()))
						temp.get().hidePlayer(gp.get());
				}
			}
			
			
			
		}
		
		
		
		
	}
	
	public void GameQuit(GamePlayer gp){
		gp.setGameQuit();
		players.remove(gp);
		GameTeam gt = gp.getTeam();
		if(gt != null){
			gt.DiePlayer(gp);
		}
		
		Core.get().getBc().quitSend(gp.get());
		
		if(this.getGameStateString().contains("游戏")){
			checkWinning();
		}
	
		if(!gp.isSpec() && !teams.contains(gt)){
			shouldUpdateDetail = true;
			updateScoreboard(false);
		}
	}
	
	public void joinSpec(GamePlayer gp) {
		gp.setGameSpec();
		GameTeam gt = gp.getTeam();
		if(gt != null){
			gt.DiePlayer(gp);
		}
		checkWinning();
		
		if(gp.getTeam() == null){
			shouldUpdateDetail = true;
			updateScoreboard(false);
		}else{
			updateOneScoreboard(gp);
		}
		
		for(GamePlayer temp : players){
			if(!temp.isSpec()){
				temp.get().hidePlayer(gp.get());
				continue;
			}
			if(temp != gp){
				if(gp.get().canSee(temp.get()))
					gp.get().hidePlayer(temp.get());
				
				if(temp.get().canSee(gp.get()))
					temp.get().hidePlayer(gp.get());
			}
		}
		
	}
	
	private boolean canBegin(){
		if(players.size() >= PeopleCouldStart && gameState == GameState.Lobby ){
			return true;
		}
		return false;
	}
	
	
	private Scoreboard getEmptyScoreBoard(GameTeam gt){
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Team ally = board.registerNewTeam("队友");
		Team enemy = board.registerNewTeam("敌人");
		Team spec = board.registerNewTeam("观战");
		
		enemy.setPrefix(ChatColor.RED.toString() );
		ally.setPrefix(ChatColor.DARK_PURPLE + ChatColor.BOLD.toString()  + "队伍 " + ChatColor.GREEN );
		
		if(gt ==null){
			if(this.getGameStateString().contains("游戏")){
				for(GamePlayer gp : players){		
					if(!gp.isSpec()){
						enemy.addEntry(gp.get().getName());
					}else{
						spec.addEntry(gp.get().getName());
					}
				}
			}else{
				for(GamePlayer gp : players){		
					enemy.addEntry(gp.get().getName());
				}
			}
		}else{
			for(GamePlayer gp : players){
				if(gt.isInTeam(gp)){
					ally.addEntry(gp.get().getName());
				}else{
					if(!gp.isSpec()){
						enemy.addEntry(gp.get().getName());
					}else{
						spec.addEntry(gp.get().getName());
					}
				}
			}
		}
	
		
		Objective ob = board.registerNewObjective("Sidder", "dummy");
		ob.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "YourCraft");
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		/*
		if(gameState != GameState.Lobby && gameState != GameState.Generating && gameState != GameState.Starting  ){
			Objective health = board.registerNewObjective("Health", "health");
			health.setDisplaySlot(DisplaySlot.BELOW_NAME);
			health.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "❤");
			new BukkitRunnable(){
				public void run(){
					for(GamePlayer gp: players){	
						try{
							WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore();
							packet.setObjectiveName("Health");
							packet.setScoreName(gp.get().getName());
							packet.setScoreboardAction(ScoreboardAction.CHANGE);
							packet.setValue((int) gp.get().getHealth());
							
							for(GamePlayer temp: players){
								if(temp == gp){
									continue;
								}
								packet.sendPacket(temp.get());
							}
							
							
						}catch(Exception e){
							
						}
					
					}
				}
			}.runTaskAsynchronously(Core.get());
		}
		*/
		
		return board;
	}
	
	private void updateOneScoreboard(GamePlayer gp){
		switch(gameState){
			case Nopvp:
				Scoreboard sb = getEmptyScoreBoard(null);
				Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
				
				Score s = obj.getScore("   ");
				s.setScore(13);
				
				
				s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "幸存队伍" );
				s.setScore(12);
				
				s = obj.getScore(teams.size()+ " 队");
				s.setScore(11);
				
				s = obj.getScore(" ");
				s.setScore(10);
				
				s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "边境");
				s.setScore(9);
				
				s = obj.getScore("-" + this.priviousBorderScore/2 + " +" + this.priviousBorderScore/2 );
				s.setScore(8);
				
				s = obj.getScore("  ");
				s.setScore(7);
				
				s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
				s.setScore(6);
				obj.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "保护结束 " + ChatColor.RED + publicCount + ChatColor.GREEN  + ChatColor.BOLD.toString() + " 秒");

				gp.get().setScoreboard(sb);
				break;
			
			case Gaming:
				 sb = getEmptyScoreBoard(null);
				 obj = sb.getObjective(DisplaySlot.SIDEBAR);
				
				 s = obj.getScore("   ");
				s.setScore(13);
				
				
				s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "幸存队伍" );
				s.setScore(12);
				
				s = obj.getScore(teams.size()+ " 队");
				s.setScore(11);
				
				s = obj.getScore(" ");
				s.setScore(10);
				
				s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "边境");
				s.setScore(9);
				
				s = obj.getScore("-" + this.priviousBorderScore/2 + " +" + this.priviousBorderScore/2 );
				s.setScore(8);
				
				s = obj.getScore("  ");
				s.setScore(7);
				
				s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
				obj.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "战斗阶段");

				s.setScore(6);
				gp.get().setScoreboard(sb);
				break;
			case Finish:
				sb = getEmptyScoreBoard(null);
				obj = sb.getObjective(DisplaySlot.SIDEBAR);
				
				s = obj.getScore("   ");
				s.setScore(13);
				
				
				s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "幸存队伍" );
				s.setScore(12);
				
				s = obj.getScore(teams.size()+ " 队");
				s.setScore(11);
				
				s = obj.getScore(" ");
				s.setScore(10);
				
				s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "边境");
				s.setScore(9);
				
				s = obj.getScore("-" + this.priviousBorderScore/2 + " +" + this.priviousBorderScore/2 );
				s.setScore(8);
				
				s = obj.getScore("  ");
				s.setScore(7);
				
				s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
				s.setScore(6);
				obj.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "保护结束 " + ChatColor.RED + publicCount + ChatColor.GREEN  + ChatColor.BOLD.toString() + " 秒");
				gp.get().setScoreboard(sb);
				
				break;
			}
	}
	
	public void updateScoreboard(boolean SpecOnly){
		
		switch(gameState){
			case Generating:
				if(shouldUpdateDetail){
					for(GamePlayer gp : players){
						Scoreboard sb = getEmptyScoreBoard(gp.getTeam());
						Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
						

						Score s = obj.getScore("   ");
						s.setScore(13);
						
						
						s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "玩家" );
						s.setScore(12);
						
						s = obj.getScore(players.size() + "/" + MaxPeopleCount);
						s.setScore(11);
						
						s = obj.getScore(" ");
						s.setScore(10);
						
						s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() +"职业");
						s.setScore(9);
						
						s = obj.getScore("无");
						s.setScore(8);
						
						s = obj.getScore("  ");
						s.setScore(7);
						
						s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
						s.setScore(6);
						obj.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + ""+ Core.get().getMc().getPercentage() + " 地图生成中...");
						gp.get().setScoreboard(sb);
					}
				}else{
					for(GamePlayer gp : players){
						try{
							Objective obj = gp.get().getScoreboard().getObjective(DisplaySlot.SIDEBAR);
							obj.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + ""+ Core.get().getMc().getPercentage() + " 地图生成中...");
						}catch(Exception e){}
					}
				}
				break;
			case Lobby:
					for(GamePlayer gp : players){
						Scoreboard sb = getEmptyScoreBoard(gp.getTeam());
						Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
						
						Score s = obj.getScore("   ");
						s.setScore(13);
						
						
						s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "玩家" );
						s.setScore(12);
						
						s = obj.getScore(players.size() + "/" + MaxPeopleCount);
						s.setScore(11);
						
						s = obj.getScore(" ");
						s.setScore(10);
						
						s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() +"职业");
						s.setScore(9);
						
						s = obj.getScore("无");
						s.setScore(8);
						
						s = obj.getScore("  ");
						s.setScore(7);
						
						s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
						s.setScore(6);
						obj.setDisplayName(ChatColor.GREEN + "等待玩家中");
						gp.get().setScoreboard(sb);
					}
				
				break;
			case Starting:
				if(shouldUpdateDetail){
					for(GamePlayer gp : players){
						Scoreboard sb = getEmptyScoreBoard(gp.getTeam());
						Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
						
						Score s = obj.getScore("   ");
						s.setScore(13);
						
						
						s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "玩家" );
						s.setScore(12);
						
						s = obj.getScore(players.size() + "/" + MaxPeopleCount);
						s.setScore(11);
						
						s = obj.getScore(" ");
						s.setScore(10);
						
						s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() +"职业");
						s.setScore(9);
						
						s = obj.getScore("无");
						s.setScore(8);
						
						s = obj.getScore("  ");
						s.setScore(7);
						
						s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
						s.setScore(6);
						obj.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "距离开始 " + publicCount +" 秒...");
						gp.get().setScoreboard(sb);
					}
				}else{
					for(GamePlayer gp : players){
						try{
							Objective obj = gp.get().getScoreboard().getObjective(DisplaySlot.SIDEBAR);
							obj.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "距离开始 " + publicCount +" 秒...");
						}catch(Exception e){}
					}
				}
				break;
			case Teleporting:
				if(shouldUpdateDetail){
					for(GamePlayer gp : players){
						Scoreboard sb = getEmptyScoreBoard(gp.getTeam());
						Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
						
						Score s = obj.getScore("   ");
						s.setScore(13);
						
						
						s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "玩家" );
						s.setScore(12);
						
						s = obj.getScore(players.size() + "/" + MaxPeopleCount);
						s.setScore(11);
						
						s = obj.getScore(" ");
						s.setScore(10);
						
						s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() +"职业");
						s.setScore(9);
						
						s = obj.getScore("无");
						s.setScore(8);
						
						s = obj.getScore("  ");
						s.setScore(7);
						
						s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
						s.setScore(6);
						obj.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "传送您至地图中...");
						gp.get().setScoreboard(sb);
					}
				}else{
					for(GamePlayer gp : players){
						try{
							Objective obj = gp.get().getScoreboard().getObjective(DisplaySlot.SIDEBAR);
							obj.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "传送您至地图中...");
						}catch(Exception e){}
					}
				}
				break;
			case Nopvp:
				if(shouldUpdateDetail){
					if(!SpecOnly){
						for(GameTeam gt : teams){
							Scoreboard sb = getEmptyScoreBoard(gt);
							Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
							
							Score s = obj.getScore("   ");
							s.setScore(13);
							
							
							s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "幸存队伍" );
							s.setScore(12);
							
							s = obj.getScore(teams.size()+ " 队");
							s.setScore(11);
							
							s = obj.getScore(" ");
							s.setScore(10);
							
							s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "边境");
							s.setScore(9);
							
							s = obj.getScore("-" + this.priviousBorderScore/2 + " +" + this.priviousBorderScore/2 );
							s.setScore(8);
							
							s = obj.getScore("  ");
							s.setScore(7);
							
							s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
							s.setScore(6);
							obj.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "保护结束 " + ChatColor.RED + publicCount + ChatColor.GREEN  + ChatColor.BOLD.toString() + " 秒");
							gt.setAllScoreBoard(sb);
						}
					}
					for(GamePlayer gp : players){
						if(gp.isSpec()){
							Scoreboard sb = getEmptyScoreBoard(null);
							Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
							
							Score s = obj.getScore("   ");
							s.setScore(13);
							
							
							s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "幸存队伍" );
							s.setScore(12);
							
							s = obj.getScore(teams.size()+ " 队");
							s.setScore(11);
							
							s = obj.getScore(" ");
							s.setScore(10);
							
							s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "边境");
							s.setScore(9);
							
							s = obj.getScore("-" + this.priviousBorderScore/2 + " +" + this.priviousBorderScore/2 );
							s.setScore(8);
							
							s = obj.getScore("  ");
							s.setScore(7);
							
							s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
							s.setScore(6);
							obj.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "保护结束 " + ChatColor.RED + publicCount + ChatColor.GREEN  + ChatColor.BOLD.toString() + " 秒");
							gp.get().setScoreboard(sb);
						}
					}
				}else{
					for(GamePlayer gp : players){
						try{
							Objective obj = gp.get().getScoreboard().getObjective(DisplaySlot.SIDEBAR);
/*
							gp.get().getScoreboard().resetScores("-" + this.priviousBorderScore/2 + " +" + this.priviousBorderScore/2   );
							Score s = obj.getScore("-" + this.currentBorderScore/2 + " +" + this.currentBorderScore/2 );
							s.setScore(8);
*/						
							obj.setDisplayName(ChatColor.GREEN  + ChatColor.BOLD.toString() + "保护结束 " + ChatColor.RED + publicCount + ChatColor.GREEN  + ChatColor.BOLD.toString() + " 秒");
						}catch(Exception e){}
					}
				}
				break;
			
			case Gaming:
				if(shouldUpdateDetail){
					if(!SpecOnly){
						
						for(GameTeam gt : teams){
							Scoreboard sb = getEmptyScoreBoard(gt);
							Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
							
							Score s = obj.getScore("   ");
							s.setScore(13);
							
							
							s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "幸存队伍" );
							s.setScore(12);
							
							s = obj.getScore(teams.size()+ " 队");
							s.setScore(11);
							
							s = obj.getScore(" ");
							s.setScore(10);
							
							s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "边境");
							s.setScore(9);
							
							s = obj.getScore("-" + this.priviousBorderScore/2 + " +" + this.priviousBorderScore/2 );
							s.setScore(8);
							
							s = obj.getScore("  ");
							s.setScore(7);
							
							s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
							s.setScore(6);
							obj.setDisplayName(ChatColor.GREEN  + ChatColor.BOLD.toString() + "战斗阶段");
							gt.setAllScoreBoard(sb);
						}
					}
					for(GamePlayer gp : players){
						if(gp.isSpec()){
							Scoreboard sb = getEmptyScoreBoard(null);
							Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
							
							Score s = obj.getScore("   ");
							s.setScore(13);
							
							
							s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "幸存队伍" );
							s.setScore(12);
							
							s = obj.getScore(teams.size()+ " 队");
							s.setScore(11);
							
							s = obj.getScore(" ");
							s.setScore(10);
							
							s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "边境");
							s.setScore(9);
							
							s = obj.getScore("-" + this.priviousBorderScore/2 + " +" + this.priviousBorderScore/2 );
							s.setScore(8);
							
							s = obj.getScore("  ");
							s.setScore(7);
							
							s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
							s.setScore(6);
							obj.setDisplayName(ChatColor.GREEN  + ChatColor.BOLD.toString() + "战斗阶段");
				
							gp.get().setScoreboard(sb);
						}
					}
				}else{
					for(GamePlayer gp : players){
						try{
							Objective obj = gp.get().getScoreboard().getObjective(DisplaySlot.SIDEBAR);
						if(priviousBorderScore != (int)border.getSize() ){
							//System.out.print("Try to update --> yes");
							gp.get().getScoreboard().resetScores("-" + this.priviousBorderScore/2 + " +" + this.priviousBorderScore/2);
							
							priviousBorderScore = (int) border.getSize();
							Score s = obj.getScore("-" + this.priviousBorderScore/2 + " +" + this.priviousBorderScore/2 );
							s.setScore(8);
							
						}else{
							//System.out.print("Try to update --> no");

						}
							
							obj.setDisplayName(ChatColor.GREEN  + ChatColor.BOLD.toString() + "战斗阶段");
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
				break;
			case Finish:
				if(shouldUpdateDetail){
						for(GameTeam gt : teams){
							Scoreboard sb = getEmptyScoreBoard(gt);
							Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
							
							Score s = obj.getScore("   ");
							s.setScore(13);
							
							
							s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "幸存队伍" );
							s.setScore(12);
							
							s = obj.getScore(teams.size()+ " 队");
							s.setScore(11);
							
							s = obj.getScore(" ");
							s.setScore(10);
							
							s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "边境");
							s.setScore(9);
							
							s = obj.getScore("-" + this.priviousBorderScore/2 + " +" + this.priviousBorderScore/2 );
							s.setScore(8);
							
							s = obj.getScore("  ");
							s.setScore(7);
							
							s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
							s.setScore(6);
							obj.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "您会在 " + publicCount + " 秒内传送");
							gt.setAllScoreBoard(sb);
						}
					
						for(GamePlayer gp : players){
							if(gp.isSpec()){
								Scoreboard sb = getEmptyScoreBoard(null);
								Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
								
								Score s = obj.getScore("   ");
								s.setScore(13);
								
								
								s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "幸存队伍" );
								s.setScore(12);
								
								s = obj.getScore(teams.size()+ " 队");
								s.setScore(11);
								
								s = obj.getScore(" ");
								s.setScore(10);
								
								s = obj.getScore(ChatColor.YELLOW + ChatColor.BOLD.toString() + "边境");
								s.setScore(9);
								
								s = obj.getScore("-" + this.priviousBorderScore/2 + " +" + this.priviousBorderScore/2 );
								s.setScore(8);
								
								s = obj.getScore("  ");
								s.setScore(7);
								
								s = obj.getScore(ChatColor.AQUA + "www.mcndsj.com");
								s.setScore(6);
								obj.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "您会在 " + publicCount + " 秒内传送");
								gp.get().setScoreboard(sb);
							}
						
					}
				}else{
					for(GamePlayer gp : players){
						try{
							Objective obj = gp.get().getScoreboard().getObjective(DisplaySlot.SIDEBAR);
							
							
							obj.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "您会在 " + publicCount + " 秒内传送");
						
							
						}catch(Exception e){}
					}
				}
				break;
		}
		this.shouldUpdateDetail = false;
		
	
	}
	
	public void updatePlayerInGame(){
		for(GamePlayer gp : players){
			if(gp.isSpec())
				continue;
			
			try{
				gp.get().setCompassTarget(gp.getTeam().getFirstButNot(gp).get().getLocation());
				if(gp.get().getItemInHand()!=null && gp.get().getItemInHand().getType() == Material.COMPASS){
					gp.sendActionBar(ChatColor.BOLD + "==== 您的队友距离您 " + ChatColor.GREEN + ChatColor.BOLD +  (int)gp.get().getCompassTarget().distance(gp.get().getLocation()) + ChatColor.WHITE  + ChatColor.BOLD + " 米 ====");
				}
			}catch(Exception e){
				
			}
			if(gp.get().getLocation().getY() > 208){ // define!
				gp.get().damage(1);
			}
		}
	}
	
	public void checkSpecOutOfBound(){
		for(GamePlayer gp : players){
			if(!gp.isSpec())
				continue;
			
			GamePlayer target = EntityUtil.getNearBySurvivalPlayer(gp);
			
			if(target != null && target.get().isOnline() && target.get().getGameMode() != GameMode.SPECTATOR ){
				gp.sendActionBar(ChatColor.BOLD +"玩家: "+ ChatColor.GREEN +  target.get().getName()
						+ ChatColor.WHITE+ ChatColor.BOLD + " 生命值: "  + ChatColor.GREEN + ChatColor.BOLD.toString() +  (int)((target.get().getHealth()/target.get().getMaxHealth()) * 100) + "%"  
						+ ChatColor.WHITE+ ChatColor.BOLD + " 距离: " + ChatColor.GREEN + ChatColor.BOLD.toString() + df.format(target.get().getLocation().distance(gp.get().getLocation())) 
						+ ChatColor.WHITE + ChatColor.BOLD );
			}else{
				gp.sendActionBar(Core.getMsg().getSpecMsg());
			}
			Location l = gp.get().getLocation();
			Location d = new Location(l.getWorld(),0,l.getY(),0);
			if(Math.abs(l.getBlockX()) >= ((border.getSize()/2)+10) || Math.abs(l.getBlockZ()) >= ((border.getSize()/2)+10)){
				try{
					gp.get().teleport(getFirstPlayer().get());
				}catch(Exception e){
					gp.get().teleport(d);
				}
			}
		}
	}
	
	private void runTask(GameState to){
		switch(to){
			case Starting:
				currentTask =new BukkitRunnable(){
					int count = startCounting;
					@Override
					public void run() {
						publicCount = count;
						if(count > 0){
							if(players.size()  < PeopleCouldStart){	
								changeState(GameState.Lobby);
								cancel();
							}else if(players.size() == MaxPeopleCount && count > 30){
								sendToAllMessage(Core.getMsg().getMessage("time-jump-to-30"));
								sendToAllSound(Sound.PISTON_EXTEND);
								count = 30;
							}
							
							if(count == 90 || count == 60 || count == 30|| count == 10 ){
								sendToAllMessage(Core.getMsg().getMessage("start-count-msg3") + count + Core.getMsg().getMessage("start-count-msg4"));
								sendToAllTitle(String.valueOf(count));
								sendToAllSound(Sound.CLICK);
							}
							
							
							
							if(count == 30){
								Core.get().getChunk().startLoadingTask();
							}
							
							if(count == 20){
								for(GameTeam gt : teams){
									gt.loadSpawn();
								}
							}
							
						
							if(count < 10){
								sendToAllTitle(String.valueOf(count));
								sendToAllSound(Sound.CLICK);
							}
							sendToAllActionBarAndExp(Core.getMsg().getMessage("start-count-msg1") + count + Core.getMsg().getMessage("start-count-msg2") , count);
							
						}else {
							sendToAllEXP(0);
							
							changeState(GameState.Teleporting);
							cancel();
						}
						
						updateScoreboard(false);
						
						count --;
						
					}
					
				}.runTaskTimer(Core.get(), 0, 20);
				break;
			case Teleporting:
				try{currentTask.cancel();} catch(Exception e){};

				currentTask =new BukkitRunnable(){
					int count = 1;
					@Override
					public void run() {
						int thisTeleport = 0;
						for(GameTeam gt : teams){
							if(gt.isTeleported){
								if(teams.indexOf(gt) == teams.size() -1){
									changeState(GameState.Nopvp);
									cancel();
								}
								continue;
							}
							thisTeleport ++;
							gt.setTeamTeleporting();
							if(thisTeleport >= count){
								break;
							}
						}
						updateScoreboard(false);
					}
					
				}.runTaskTimer(Core.get(), 0, 5);
				break;
			case Nopvp:
				try{currentTask.cancel();} catch(Exception e){};
			
				currentTask =new BukkitRunnable(){
					int count = 600 ;
					
					@Override
					public void run() {
						checkSpecOutOfBound();
						updatePlayerInGame();
						sendChestParticlePacket();
						
						//currentBorderScore = (int) border.getSize();
					
						
						if(count > 0){
							if(count == 300 || count == 120 || count == 60){
								String s = Core.getMsg().getMessage("wall-fall-time3") + count +Core.getMsg().getMessage("wall-fall-time4");
								sendToAllSoundAndMsgAndTitle(Sound.CLICK,s ,s);
							}
							
							if(count < 10){
								sendToAllSound(Sound.ARROW_HIT);
							}
							publicCount = count;
							updateScoreboard(false);
							
							count --;
							
							return;
						}
						
						changeState(GameState.Gaming);
						updateScoreboard(false);
						cancel();
					}
					
				}.runTaskTimer(Core.get(), 0, 20);
				break;
				
			case Gaming:
				try{currentTask.cancel();} catch(Exception e){};
				currentTask =new BukkitRunnable(){
					int count = 0;
					boolean fastEnd = false;
					boolean isFinal1VS1 = false;
					int type = -1;
					@Override
					public void run() {
						checkSpecOutOfBound();
						updatePlayerInGame();
						sendChestParticlePacket();
						
						if(border.getSize() % 10 == 0){
							System.out.print("当前border = " + border.getSize() + " 当前Pri = " + priviousBorderScore);
						}
						//currentBorderScore = (int) border.getSize();
						updateScoreboard(false);
						//System.out.print("updateDone");

						
						count ++;
						/*
						if(border.getSize() <= 20 && !fastEnd){
							Random r = new Random();
							type = r.nextInt(3);
							
							if(howManySurvival() > 2){
								if(type == 0){
									sendToAllMessage(ChatColor.RED + " 边境足够小了,现在会对Y轴坐标最大和最小,也就是高度最高和最低的玩家持续造成伤害!");
								}else if(type == 1){
									sendToAllMessage(ChatColor.RED + " 边境足够小了,现在会有无数的TNT从天而降,祝你们好运.");
								}else if(type == 2){
									sendToAllMessage(ChatColor.RED + " 边境足够小了,现在泥土开始松动了,注意不要被埋葬哦!");
								}
							}
							
							fastEnd = true;
							return;
						}
						*/
						
						if(count <= 5){
							return;
						}
						
					
						
						
						if(border.getSize() <= 40){
							if(type == -1){
								return;
							}
							if(type == 0){
								if(howManySurvival() > 2){
									GamePlayer upper = null;
									GamePlayer lower = null;
									
									for(GamePlayer gp : players){
										if(gp.isSpec()){
											continue;
										}
										
										if(upper ==null || lower == null){
											upper = gp;
											lower = gp;
											continue;
										}
										double gpY = gp.get().getLocation().getY();
										if( gpY > upper.get().getLocation().getY()){
											upper = gp;
										}
										
										if(gpY < lower.get().getLocation().getY()){
											lower = gp;
										}
										
									}
									if(upper != lower){
										if(upper !=null ){
											if(upper.get().getGameMode() != GameMode.SPECTATOR){
												upper.get().getPlayer().damage(1);
											}
											
										}
										
										if(lower !=null ){
											if(lower.get().getGameMode() != GameMode.SPECTATOR){
												lower.get().getPlayer().damage(1);
											}
										}
										sendToAllSound(Sound.GHAST_SCREAM);
										sendToAllMessage(ChatColor.RED + " 玩家 " + ChatColor.YELLOW + upper.get().getName() + ChatColor.RED + " 与 " + ChatColor.YELLOW + lower.get().getName() + ChatColor.RED + " 受到Y轴边境伤害.");
									}
								}else {
									if(!isFinal1VS1){
										sendToAllMessage(ChatColor.RED + " 看来只有两个人存活了,持续掉血限制移除,决战吧!");
										isFinal1VS1 = true;
									}
								}
							}else if(type == 1){
								new BukkitRunnable(){
									int c =  0;

									@Override
									public void run() {
										c ++;
										if(c == 3){
											cancel();
										}
										for(int i = 0 ; i < 2 ; i ++){
											Location l = Core.get().getMc().getRandomLocation((int)border.getSize(), 0);
											l.setY(254);
											FallingBlock fb =l.getWorld().spawnFallingBlock(l, Material.TNT, (byte) 0);
											fb.setMetadata("TNT_PRIMED", new FixedMetadataValue(Core.get(),"123"));
											fb.setCustomName(ChatColor.RED + ChatColor.BOLD.toString() + "炸弹");
											fb.setCustomNameVisible(true);
											
								        }
										sendToAllSound(Sound.GHAST_SCREAM);
										sendToAllMessage(ChatColor.RED +"TNT雨来了,快躲避!");
									}
									
								}.runTaskTimer(Core.get(), 0, 10);
							}else  if(type == 2){
								new BukkitRunnable(){
									int c =  0;
									
									@Override
									public void run() {
										
										c ++;
										if(c == 3){
											cancel();
										}
										for(GamePlayer gp : players){
											if(gp.isSpec()){
												continue;
											}
											Location l = gp.get().getEyeLocation();
											while(l.getBlock().getType() == Material.AIR){
												l.setY(l.getY() + 1);
												if(l.getY() >= 255){
													break;
												}
											}
											if(l.getY() >= 255){
												continue;
											}
											FallingBlock fb =l.getWorld().spawnFallingBlock(l, l.getBlock().getType(), (byte) l.getBlock().getData());
											l.getBlock().setType(Material.AIR);

											fb.setMetadata("HEAVY_SAND", new FixedMetadataValue(Core.get(),"123"));
											fb.setCustomName(ChatColor.RED + ChatColor.BOLD.toString() + "坍塌方块");
											fb.setCustomNameVisible(true);
											/*
											if(gp.get().getLocation().getY() < 20){
												gp.get().damage(5);
											}
											*/
											
											
										}
									
								        
										sendToAllSound(Sound.GHAST_SCREAM);
										sendToAllMessage(ChatColor.RED +"注意头上的方块，他要坍下来了!");

										
									}
									
								}.runTaskTimer(Core.get(), 0, 10);
							}
								
							
						}else{
							border.setSize(border.getSize() - (((double)Core.boarderSize/(double)shrinkTime) *(count+1)) , 3);
						}
						count = 0;
					
						
						
						

					
						
						
						
					}
					
				}.runTaskTimer(Core.get(), 0, 20);
				break;
			case Finish:
				try{currentTask.cancel();} catch(Exception e){};
			
				currentTask =new BukkitRunnable(){
					int count = 20 ;
					@Override
					public void run() {
						
						if(count > 0){
							for(GamePlayer temp : players){
								if(!temp.isSpec()){
									FireWorkUlt.spawnFireWork(temp.get().getLocation(), temp.get().getWorld());
								}
							}
							publicCount = count;
							updateScoreboard(false);
							count --;
							return;
						}
						
						
						
						for(GamePlayer temp : players){
							Core.get().getBc().quitSend(temp.get());
						}
						
						// shut-up task
				
						new BukkitRunnable(){
							int count = 10;
							public void run(){
								
								count --;
								
								if(count <=0){
									Bukkit.shutdown();
								}
							}
						}.runTaskTimer(Core.get(), 0, 20);
					
					
					}
					
				}.runTaskTimer(Core.get(), 0, 20);
				break;
		}
	}
	
	public void changeState(GameState to){
		switch(to){
			case Generating:
				try{
				if(Core.get().getMc().isGood()){
					System.out.print("ERROR -> Good but generating!");
					return;
				}}catch(Exception e){e.printStackTrace(); System.out.print("first");}
				
				Core.get().getMc().beginGenerate();
				try{
				gameState = GameState.Generating;
				updateScoreboard(false);
				}catch(Exception e){e.printStackTrace();}
				break;
			case Lobby:
				gameState = GameState.Lobby;
				Core.get().getChunk().preLoadLocations();
				if(canBegin()){
					gameState = GameState.Starting;
					runTask(GameState.Starting);
						
				}else{
					for(GamePlayer gp : players){
						gp.get().setExp(0);
						gp.get().setLevel(0);
						
					}
				}
				updateScoreboard(false);
				break;
			case Starting:
				gameState = GameState.Starting;
				runTask(GameState.Starting);
				break;
			case Teleporting:
				//NOTE: THIS IS SUPER LAGGY, MIGHT NEED TO DISABLE ANTI-CHEAT PLUGIN HERE
				gameState = GameState.Teleporting;
				Core.get().getMc().getWorld().setTime(0);
				
				teamAll();
				/*
				for(GameTeam gt : teams){
					gt.generateSpawnLocation();
				}
				*/
				
					for(GamePlayer gp : players){
						for(int i = 0 ; i < 20 ; i ++){
							gp.get().sendMessage( " ");
						}
					}
				
			
				new BukkitRunnable(){
					@Override
					public void run() {
						if(gameState != GameState.Teleporting){
							cancel();
						}
						for(GameTeam gt : teams){
							sendToAllTitle(ChatColor.RED + "传送中……请双手离开键盘,避免掉线.");
							sendToAllActionBar(ChatColor.GREEN + ChatColor.BOLD.toString() + "传送中 " + ActionBarUtil.getActionBarString(Double.valueOf((double)teams.indexOf(gt) / (double)teams.size()))  );
						}
					}
					
				}.runTaskTimerAsynchronously(Core.get(), 0, 20);
				
				
				runTask(GameState.Teleporting);
				shouldUpdateDetail = true;
				updateScoreboard(false);
				break;
			case Nopvp:
				for(GamePlayer gp : players){
					gp.setGameBegin();
					gp.getGs().addgames();
					gp.sendTitle(ChatColor.RED + "游戏开始,祝你好运!");
				}
				
				gameState = GameState.Nopvp;
				Core.get().getChunk().clearChunkCache();
				
				runTask(GameState.Starting);
				
				
				setUpBoarder();
				setUpChest();
				
				runTask(GameState.Nopvp);
				shouldUpdateDetail = true;
				updateScoreboard(false);
				break;
				
			case Gaming:
				gameState = GameState.Gaming;
				//border.setSize(20, shrinkTime);
				this.sendToAllMessage(" 边境开始收缩!");
				sendToAllSound(Sound.ENDERMAN_TELEPORT);
				runTask(GameState.Gaming);
				shouldUpdateDetail = true;
				updateScoreboard(false);
				break;
			case Finish:
				gameState = GameState.Finish;
				Location toP = null;
				sendToAllSound(Sound.WITHER_DEATH);
				for(GamePlayer gp : players){
					
					if(!gp.isSpec()){
						gp.sendTitle(ChatColor.RED + "你赢了!");
						gp.getGs().addWin();
						toP = gp.get().getLocation();
						try{
							Core.economy.depositPlayer(gp.get(), 10);
						}catch(Exception e){e.printStackTrace();};
					}
				}
				if(toP != null){
					for(GamePlayer gp : players){
						if(gp.isSpec()){
							gp.get().teleport(LocationUtil.getNearBySaftLocation(toP.getWorld(), toP.getBlockX(), toP.getBlockZ(), 10,false));
						}
					}
				}
				chestTask.cancel();
				Core.get().getMc().getWorld().setTime(13000);
				runTask(GameState.Finish);
				updateScoreboard(false);
				break;
		}
	}
	
	
	public void checkWinning(){
		if(gameState == GameState.Finish){
			return;
		}
	
		for(GameTeam gt : teams){
			gt.CheckDead();
		}
		if(teams.size() == 1){
			
			changeState(GameState.Finish);
		}
	}
	
	
	private void setUpChest(){
		chestTask = new BukkitRunnable(){
			int count = 6;
			Location l = null;
			@Override
			public void run() {
				if(l == null){
					l = Core.get().getMc().getRandomLocation((int)(border.getSize() - 60*3*Core.boarderSize/shrinkTime),0);
					l = l.getWorld().getHighestBlockAt(l).getLocation();
				}
				count --;
				if(count > 0){
					sendToAllMessage(ChatColor.RED + " 援助资源箱将在 "+ ChatColor.GREEN  +count + ChatColor.RED + " 分钟后于 "+ ChatColor.GREEN + "x:" + l.getBlockX()+ " y:" + l.getBlockY() + " z:" + l.getBlockZ() + ChatColor.RED+ " 处空投.内有钻石、回血药水、金苹果等稀有物品！");
					return;
				}
				try{
					sendToAllMessage(ChatColor.RED + " 援助资源箱已经在  "+ ChatColor.GREEN + "x:" + l.getBlockX()+ " y:" + l.getBlockY() + " z:" + l.getBlockZ() + ChatColor.RED+ " 处空投,请周围生存玩家注意收取宝箱内极品道具,注意抬头的时候不要被空投物资砸到!");
					l.setY(l.getY() + 20);
					FallingBlock fb =l.getWorld().spawnFallingBlock(l, Material.SAND, (byte) 0);
					fb.setMetadata("CHEST_FALL", new FixedMetadataValue(Core.get(),"123"));
					fb.setCustomName("空投物资");
					fb.setCustomNameVisible(true);
					
					/*
					l.getBlock().setType(Material.CHEST);
					Chest c = (Chest) l.getBlock().getState();
					c.getInventory().setContents(Core.get().getCc().generateLoot(26));
					sendToAllMessage(ChatColor.RED + " 援助资源箱已经在  "+ ChatColor.GREEN + "x:" + l.getBlockX()+ " y:" + l.getBlockY() + " z:" + l.getBlockZ() + ChatColor.RED+ " 处空投,请周围生存玩家注意收取宝箱内极品道具!");
					sendToAllSound(Sound.ANVIL_LAND);
					*/

					
				}catch(Exception e){
					e.printStackTrace();
					sendToAllMessage(ChatColor.RED + " 空投飞机出了一些意外，已经坠机了！");

				}
			
				l = null;
				count = 4;
				
			}
			
		}.runTaskTimer(Core.get(), 0, 20 * 60);
	}
	
	
	private void setUpBoarder(){
		
		border = Core.get().getMc().getWorld().getWorldBorder();
		border.reset();
		
		border.setCenter(0, 0);
		border.setSize(Core.boarderSize);
		border.setDamageAmount(1);
		border.setDamageBuffer(0);
		border.setWarningDistance(10);
		border.setWarningTime(5);
		
		this.priviousBorderScore = (int) border.getSize();
		
	}
	
	private void teamAll(){
		List<GamePlayer> notInTeam = new ArrayList<GamePlayer>();
		for(GamePlayer gp : players){
			if(!gp.isInTeam()){
				notInTeam.add(gp);
				System.out.print("Add " + gp.get().getName());
			}
		}
		for(GameTeam gt : teams){
			try{
				while(gt.JoinTeam(notInTeam.get(0),false)){
					//System.out.print(notInTeam.get(0) + " join exsisting team. " );
					notInTeam.remove(0);
					if(notInTeam.size() == 0){
						break;
					}
				}
			}catch(Exception e){
				
			}
		}
		
		if(notInTeam.size() > 0){
			int i = notInTeam.size();
			int couldBe = i/3;
			if(i% 3 != 0){
				couldBe ++;
			}
			GameTeam currentTeam = new GameTeam();
			for(int temp = 0; temp < couldBe ; temp ++){
				if(notInTeam.size() != 0){
					try{
						while(currentTeam.JoinTeam(notInTeam.get(0),false)){
							notInTeam.remove(0);
							if(notInTeam.size() == 0){
								break;
							}
					}}catch(Exception e){};
				}
				
				teams.add(currentTeam);
				currentTeam = new GameTeam();
			}
		}
	}
	
	
	public boolean isChestLocation(Location l){
		return chestLocations.contains(l);
	}
	
	public void removeChestLocation(Location l){
		chestLocations.remove(l);
	}
	
	public void addChestLocation(Location l){
		chestLocations.add(l);
	}
	private void sendChestParticlePacket(){
		Random r = new Random();
		for(Location l : chestLocations){
			Core.getNms().sendParticles(l.getWorld(), "VILLAGER_ANGRY",l.getBlockX() , l.getBlockY(), l.getBlockZ(), r.nextFloat(), r.nextFloat(), r.nextFloat(), 0, r.nextInt(5) + 1);

		}
	}
	
	private int howManySurvival(){
		int nowSurvival = 0;
		for(GamePlayer gps : players){
			if(!gps.isSpec()){
				nowSurvival ++;
			}
		}
		return nowSurvival;
	}
	
	public void sendToAllMessage(String msg){
		for(GamePlayer p : players){
			p.get().sendMessage(Message.prefix + msg);
		}
	}
	
	private void sendToAllActionBar(String str){
		for(GamePlayer p : players){
			p.sendActionBar(str);;
		}
	}
	
	private void sendToAllActionBarAndExp(String str, int level){
		for(GamePlayer p : players){
			p.sendActionBar(str);
			p.get().setLevel(level);
		}
	}
	
	private void sendToAllEXP(int level){
		for(GamePlayer p : players){
			p.get().setLevel(level);
		}
	}
	
	public void sendToAllSound(Sound s){
		for(GamePlayer p : players){
			p.get().playSound(p.get().getLocation(), s, 1, 1);
		}
	}
		
	private void sendToAllSoundAndMsgAndTitle(Sound s,String msg,String title){
		for(GamePlayer p : players){
			p.get().playSound(p.get().getLocation(), s, 1, 1);
			p.get().sendMessage(Message.prefix + msg);
			p.sendTitle(title);
		}
	}
	
	private void sendToAllTitle(String title){
		for(GamePlayer p : players){
			p.sendTitle(title);
		}
	}
	
	private void sendToAllTitle(String title , String small){
		for(GamePlayer p : players){
			p.sendTitle(title,small);
		}
	}
	
	public void sendAllChatMessage(String str){
		for(GamePlayer p : players){
			p.get().sendMessage(Core.getMsg().getMessage("all-msg-prefix") + str);
		}
		Bukkit.getLogger().info("AC : " + str);
	}

	public enum GameState{
		Generating,Lobby,Starting,Teleporting,Nopvp,Gaming,Finish
	}
	
	public void disbandTeam(GameTeam gt){
		shouldUpdateDetail = true;
		teams.remove(gt);
	}
	
	public GameState getGameState(){
		return gameState;
	}
	
	public String getGameStateString(){
		switch(gameState){
		case Generating:
				return  ChatColor.GREEN  + "生成地图中";
		case Lobby:
			return  ChatColor.GREEN +"等待中";
		case Starting:
			return ChatColor.GREEN  + "等待中";
		case Teleporting:
			return  ChatColor.RED  +"游戏中";
		case Nopvp:
			return ChatColor.RED+"游戏中";
		case Gaming:
			return ChatColor.RED  +"游戏中";
		case Finish:
			return ChatColor.RED  +"游戏中";
		}
		return ChatColor.RED +"游戏中";
	}

	public double getBorderSize(){
		return border.getSize();
	}
	
	
}
