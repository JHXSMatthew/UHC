package com.github.JHXSMatthew.Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.JHXSMatthew.Config.Config;
import com.github.JHXSMatthew.Game.GameStats;
import com.github.JHXSMatthew.Util.SQLStatsContainer;
import com.huskehhh.mysql.mysql.MySQL;

public class MySQLController {

	private Connection c = null;
	private MySQL my;
	private static String TABLENAME = "UHC";
	
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
		
		// 使用 PreparedStatement 防止 SQL 注入
		String query = "SELECT * FROM `" + TABLENAME + "` WHERE `Name`=?";
		PreparedStatement ps = null;
		ResultSet result = null;
		SQLStatsContainer current = new SQLStatsContainer();
		
		try {
			ps = this.c.prepareStatement(query);
			ps.setString(1, name);
			result = ps.executeQuery();
			
			if(result.next()){
				try{
					current.death = result.getInt("Deaths");
					current.wins = result.getInt("Wins");
					current.kills = result.getInt("Kills");
					current.games = result.getInt("Games");
					current.stack = result.getInt("Stacks");
					current.points = result.getInt("Points");
				}catch(SQLException e){
					System.err.println("Error reading player stats: " + e.getMessage());
					e.printStackTrace();
				}
				current.New = false;
			}
		} finally {
			// 确保资源被正确关闭
			if(result != null) {
				try { result.close(); } catch(SQLException e) { e.printStackTrace(); }
			}
			if(ps != null) {
				try { ps.close(); } catch(SQLException e) { e.printStackTrace(); }
			}
		}
		
		return current;
	}
	
	public boolean savePlayerData(GameStats data){
		PreparedStatement ps = null;
		
		try{
			String name = data.getName();
			
			if(data.isNew()){
				// INSERT 使用 PreparedStatement
				String insertQuery = "INSERT INTO `"+ TABLENAME +"` (`Name`,`Games`,`Wins`,`Kills`,`Deaths`,`Stacks`,`Points`) VALUES (?,?,?,?,?,?,?)";
				ps = this.c.prepareStatement(insertQuery);
				ps.setString(1, data.getName());
				ps.setInt(2, data.getGames());
				ps.setInt(3, data.getWins());
				ps.setInt(4, data.getKills());
				ps.setInt(5, data.getDeath());
				ps.setInt(6, data.getStack());
				ps.setInt(7, data.getPoints());
				ps.executeUpdate();
			}else{
				// UPDATE 使用 PreparedStatement
				String updateQuery;
				if(data.getStack() > data.Ori_stack){
					updateQuery = "UPDATE `"+ TABLENAME +"` SET `Games`=?,`Wins`=?,`Kills`=?,`Deaths`=?,`Stacks`=?,`Points`=? WHERE `Name`=?";
					ps = this.c.prepareStatement(updateQuery);
					ps.setInt(1, data.getGames());
					ps.setInt(2, data.getWins());
					ps.setInt(3, data.getKills());
					ps.setInt(4, data.getDeath());
					ps.setInt(5, data.getStack());
					ps.setInt(6, data.getPoints());
					ps.setString(7, name);
				}else{
					updateQuery = "UPDATE `"+ TABLENAME +"` SET `Games`=?,`Wins`=?,`Kills`=?,`Deaths`=?,`Points`=? WHERE `Name`=?";
					ps = this.c.prepareStatement(updateQuery);
					ps.setInt(1, data.getGames());
					ps.setInt(2, data.getWins());
					ps.setInt(3, data.getKills());
					ps.setInt(4, data.getDeath());
					ps.setInt(5, data.getPoints());
					ps.setString(6, name);
				}
				ps.executeUpdate();
			}
			
			return true;
		}catch(SQLException e){
			System.err.println("Error saving player data: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			// 确保资源被正确关闭
			if(ps != null) {
				try { ps.close(); } catch(SQLException e) { e.printStackTrace(); }
			}
		}
	}
}
