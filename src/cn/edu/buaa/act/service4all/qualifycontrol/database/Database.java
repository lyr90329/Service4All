package cn.edu.buaa.act.service4all.qualifycontrol.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import cn.edu.buaa.act.service4all.qualifycontrol.common.Config;
import cn.edu.buaa.act.service4all.qualifycontrol.common.Constants;


public class Database 
{
	private static Database database=null;
	private static Connection con=null;
	
	private Database()
	{
		connect();
	}
	
	private boolean connect()
	{
		try
	      {
			Class.forName(Config.jdbc);	
			con=DriverManager.getConnection(Config.url,Config.user,Config.password);	
			con.setAutoCommit(false);
			return true;
	      }
	    catch(Exception e)
	     {	
	    	e.printStackTrace();
	    	return false;
	     }
	}
	
	public static Database getInstance()
	{
		if(database==null)
			database=new Database();
		return database;
	}
	
	public synchronized String register(String username,String password)
	{
		String sql;
		PreparedStatement query=null,insert = null;
		ResultSet result=null;
		
		sql="select * from user where userName=?";
		try 
		{
			query=con.prepareStatement(sql);
			query.setString(1, username);
			result=query.executeQuery();
			if(result.next())
			{				
				return "user already exists";
			}
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			return "error at querying the existence of user";
		}
		finally
		{
			try 
			{
				if(result!=null){
					result.close();
				}
				if(query!=null){
					query.close();
				}
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}			
		}	
		
		sql="insert into user(username,password,state) values(?,?,?)";
		try
		{
			insert=con.prepareStatement(sql);
			insert.setString(1, username);
			insert.setString(2, password);
			insert.setInt(3, Constants.user_Init);
			insert.executeUpdate();
			con.commit();
			return null;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			try 
			{
				con.rollback();
			}
			catch (SQLException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "error at inserting username and password into user tables";			
		}
		finally
		{
			try 
			{
				insert.close();
			}
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized String login(String username,String password)
	{
		String state=null,sql,pwd;
		PreparedStatement query=null,update = null;
		ResultSet result=null;
		
		sql="select * from user where username=?";
		try 
		{
			query=con.prepareStatement(sql);
			query.setString(1, username);
			result=query.executeQuery();
			if(result.next())
			{	
				state=result.getString(Constants.state);
				if(Integer.parseInt(state)==Constants.user_Login)
				{	
					return "user already login";
				}
				
				pwd=result.getString(Constants.password);
				if(!pwd.equals(password))
				{
					return "password is error";
				}				
			}
			else
			{
				return "user name does not exist";
			}
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			return "error at querying the existence of user";
		}
		finally
		{
			try 
			{
				if(result!=null){
					result.close();
				}
				query.close();
			} 
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}	
		
		sql="update user set state=? where username=?";
		try
		{
			update=con.prepareStatement(sql);
			update.setInt(1, Constants.user_Login);
			update.setString(2, username);
			update.executeUpdate();
			con.commit();
			return null;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			try 
			{
				con.rollback();
			} 
			catch (SQLException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "error at logging in";			
		}
		finally
		{
			try 
			{
				update.close();
			}
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized String logout(String username)
	{
		String sql;
		PreparedStatement update = null;
			
		sql="update user set state=? where username=?";
		try
		{
			update=con.prepareStatement(sql);
			update.setInt(1, Constants.user_Logout);
			update.setString(2, username);
			update.executeUpdate();
			con.commit();
			return null;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			try 
			{
				con.rollback();
			}
			catch (SQLException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "error at logging out";			
		}
		finally
		{
			try 
			{
				update.close();
			}
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized String deployService(String username ,Vector<String> services,int type)
	{
		String sql,qualify;
		PreparedStatement insert=null;
		
		if(type!=Constants.serviceType_webservice&&type!=Constants.serviceType_BPMN&&type!=Constants.SERVICE_TYPE_WEBAPP)
		{
			return "service type is error";
		}
		
		qualify=this.deployQualification(username);
		if(qualify!=null)
			return qualify;
		
		sql="insert into service(serviceid,type,username) values(?,?,?)";
		
		try 
		{
			insert=con.prepareStatement(sql);
			insert.setInt(2, type);
			insert.setString(3, username);	
			
			for(String s:services)
			{
				insert.setString(1, s);			
				insert.addBatch();				
			}
			insert.executeBatch();
			con.commit();
			return null;
		}
		catch (SQLException e) 
		{
			try 
			{
				con.rollback();
			} 
			catch (SQLException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			return "error at insert services into service table";
		}
		finally
		{
			try 
			{
				insert.close();
			} 
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public synchronized String undeployService(String username,Vector<String> services,int type)
	{
		String sql,qualify;
		PreparedStatement insert=null;
		
		if(type!=Constants.serviceType_webservice&&type!=Constants.serviceType_BPMN&&type!=Constants.SERVICE_TYPE_WEBAPP)
		{
			return "service type is error";
		}
		
		for(String service:services)
		{
			qualify=this.undeployQualification(username, service);
			if(qualify!=null)
				return qualify;
		}
		
		sql="delete from service where username=? and serviceid=? and type=?";
		
		try 
		{
			insert=con.prepareStatement(sql);
			insert.setString(1, username);
			insert.setInt(3, type);
			for(String s:services)
			{
				insert.setString(2, s);
				insert.addBatch();				
			}
			insert.executeBatch();
			con.commit();
			return null;
		}
		catch (SQLException e) 
		{
			try 
			{
				con.rollback();
			} 
			catch (SQLException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
			return "error at deleting services from service table";
		}
		finally
		{
			try 
			{
				insert.close();
			} 
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 判断用户是否登录
	 * @param username
	 * @return
	 */
	public synchronized String deployQualification(String username)
	{
		String sql;
		PreparedStatement query = null;
		ResultSet rs=null;
			
		sql="select * from user where username=? and state=?";
		try
		{
			query=con.prepareStatement(sql);
			query.setString(1, username);
			query.setInt(2, Constants.user_Login);
			rs=query.executeQuery();
			if(rs.next())
			{
				return null;
			}
			else
			{
				return "User "+username+" doesn't login so he does not has the qualification to deploy services";
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return "error at querying deploying qualification";			
		}
		finally
		{
			try 
			{
				if(rs!=null){
					rs.close();
				}
				query.close();
			}
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 判断用户是否有反部署某个服务的权限
	 * @param username
	 * @param serviceid
	 * @return
	 */
	public synchronized String undeployQualification(String username,String serviceid)
	{
		String sql;
		PreparedStatement query = null;
		ResultSet rs=null;
			
		sql="select * from user,service where user.username=service.username and user.username=? and state=? and serviceid=?";
		try
		{
			query=con.prepareStatement(sql);
			query.setString(1, username);
			query.setInt(2, Constants.user_Login);
			query.setString(3, serviceid);
			rs=query.executeQuery();
			if(rs.next())
			{
				return null;
			}
			else
			{
				return "User "+username+" does not has the qualification to undeploy service "+serviceid;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return "error at querying undeploy qualification";			
		}
		finally
		{
			try 
			{
				rs.close();
				query.close();
			}
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 判断用户是否有调用的权限（实际是只检查了是否登录）
	 * @param username
	 * @param serviceid
	 * @return
	 */
	public synchronized String invokeQualification(String username,String serviceid)
	{
		String sql;
		PreparedStatement query = null;
		ResultSet rs=null;
			
		sql="select * from user where username=? and state=?";
		try
		{
			query=con.prepareStatement(sql);
			query.setString(1, username);
			query.setInt(2, Constants.user_Login);
			rs=query.executeQuery();
			if(rs.next())
			{
				return null;
			}
			else
			{
				return "User "+username+" does not login so he doesn't has the qualification to invoke service "+serviceid;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return "error at querying invoking qualification";			
		}
		finally
		{
			try 
			{
				rs.close();
				query.close();
			}
			catch (SQLException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}