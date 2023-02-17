package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	public List<Genre> listGenres(){
		
		List<Genre> genreList = new ArrayList<>();
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
		    try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre")){
		    	try (ResultSet resultSet = statement.executeQuery()){
				    // process the results
				    while (resultSet.next()) {
				      Genre genre = new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
				      genreList.add(genre);
				    }
		    	}
		    }
		} 
		catch (SQLException e) {
		    e.printStackTrace();
		} 
		return genreList;
	}

	public Genre getGenre(String name) {
		
		Genre genre = null;
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
		    try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name = ?")){
		    	statement.setString(1, name);
		    	try (ResultSet resultSet = statement.executeQuery()){
				    // process the results
		    		while (resultSet.next()) {
		  		      genre = new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
		  		    }
		    	}
		    }
		} 
		catch (SQLException e) {
		    e.printStackTrace();
		}
		
		return genre;
	}

	public void addGenre(String name) {
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
		    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO genre (name) VALUES (?)")){
		    	statement.setString(1, name);
		    	statement.executeUpdate();
		    }
		} 
		catch (SQLException e) {
		    e.printStackTrace();
		}
	}
}
