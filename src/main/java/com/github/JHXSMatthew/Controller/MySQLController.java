package com.github.JHXSMatthew.Controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.github.JHXSMatthew.Config.Config;
import com.github.JHXSMatthew.Game.GameStats;
import com.github.JHXSMatthew.Util.SQLStatsContainer;
import com.huskehhh.mysql.mysql.MySQL;

public class MySQLController {

	private Connection c =null;
	private MySQL my;
	private static String TABLENAME  = "UHC";
	
	public MySQLController(){
	    this.my = new MySQL(Config.SQL_ADDRESS, String.valueOf(Config.SQL_PORT)
				, Config.SQL_DB_NAME, Config.SQL_USER_NAME, Config.SQL_PASSWORD);
	    TABLENAME = Config.SQL_TABLE_NAME;
	}
	
	public void openConnection(){
	    try {
			c = my.openConnection();
			// 使用数据库初始化工具创建表
			com.github.JHXSMatthew.Utils.DatabaseInitializer initializer = 
				new com.github.JHXSMatthew.Utils.DatabaseInitializer();
			initializer.initializeTables(c);

		} catch (ClassNotFoundException e) {
			System.out.print("Connection error !");
			e.printStackTrace();
		} catch (SQLException e1) {
			System.out.print("Connection error !");
			e1.printStackTrace();
		}
	}
	
	
	public void clsoeConnection() throws SQLException{
		this.c.close();
	}
	
	
	
	public void closeDB() throws SQLException{
		this.my.closeConnection();
	}
	
	public SQLStatsContainer loadStats(String name) throws SQLException, ClassNotFoundException{
		if(!this.my.checkConnection()){
			this.c = this.my.openConnection();
		}
		Statement s = this.c.createStatement();
		ResultSet result = s.executeQuery("SELECT * FROM `" + TABLENAME + "` Where `Name`='"+name+"';");
		SQLStatsContainer current = new SQLStatsContainer();
		
		if(result.next()){
			try{
				current.death = result.getInt("Deaths");
				current.wins = result.getInt("Wins");
				current.kills = result.getInt("Kills");
				current.games = result.getInt("Games");
				current.stack = result.getInt("Stacks");
				current.points =  result.getInt("Points");
			}catch(Exception e){
				e.printStackTrace();
			}
			current.New = false;
		}
		
		s.close();
		result.close();
		s=null;
		
		return current;
		
	}
	public boolean savePlayerData(GameStats data){
		try{
			String name = data.getName();
			Statement s = this.c.createStatement();
			if(data.isNew()){
				
				s.executeUpdate("INSERT INTO `"+ TABLENAME +"` (`Name`,`Games`,`Wins`,`Kills`,`Deaths`,`Stacks`,`Points`) VALUES ('"+data.getName()+"','"+ data.getGames() + "','"+ data.getWins() + "','" + data.getKills() + "','" + data.getDeath() + "','" + data.getStack() +"','" +data.getPoints() + "');" );
			}else{
				if(data.getStack() > data.Ori_stack){
					s.executeUpdate("UPDATE `"+ TABLENAME +"` SET `Games`='"+data.getGames() +"',`Wins`='"+data.getWins()
							+"',`Kills`='"+ data.getKills() 
							+"',`Deaths`='"+ data.getDeath()
							+"',`Stacks`='"+ data.getStack()
							+"',`Points`='"+ data.getPoints()
						    + "' Where `Name`='"+name+"';");
				}else{
					s.executeUpdate("UPDATE `"+ TABLENAME +"` SET `Games`='"+data.getGames() +"',`Wins`='"+data.getWins()
							+"',`Kills`='"+ data.getKills() 
							+"',`Deaths`='"+ data.getDeath()
							+"',`Points`='"+ data.getPoints()
						    + "' Where `Name`='"+name+"';");
				}
					 
				}
			
			
			s.close();
			return true;
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		
	}
	
	

}
