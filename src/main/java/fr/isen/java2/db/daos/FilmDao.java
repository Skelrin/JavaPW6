package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

public class FilmDao {

	public List<Film> listFilms() {
		
		List<Film> filmList = new ArrayList<>();
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
		    try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre")){
		    	try (ResultSet resultSet = statement.executeQuery()){
				    // process the results
				    while (resultSet.next()) {
				    	Genre genre = new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
				    	Film film = new Film(resultSet.getInt("idfilm"), resultSet.getString("title"), resultSet.getDate("release_date").toLocalDate(), genre, resultSet.getInt("duration"), resultSet.getString("director"), resultSet.getString("summary"));
				    	filmList.add(film);
				    }
		    	}
		    }
		} 
		catch (SQLException e) {
		    e.printStackTrace();
		} 
		return filmList;
	}

	public List<Film> listFilmsByGenre(String genreName) {
		
		List<Film> filmList = new ArrayList<>();
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
		    try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM film JOIN genre ON film.genre_id = genre.idgenre WHERE genre.name = ?")){
		    	statement.setString(1, genreName);
		    	try (ResultSet resultSet = statement.executeQuery()){
				    // process the results
				    while (resultSet.next()) {
				    	Genre genre = new Genre(resultSet.getInt("idgenre"), resultSet.getString("name"));
				    	Film film = new Film(resultSet.getInt("idfilm"), resultSet.getString("title"), resultSet.getDate("release_date").toLocalDate(), genre, resultSet.getInt("duration"), resultSet.getString("director"), resultSet.getString("summary"));
				    	filmList.add(film);
				    }
		    	}
		    }
		} 
		catch (SQLException e) {
		    e.printStackTrace();
		} 
		return filmList;
	}

	public Film addFilm(Film film) {
		
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
		    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO film(title,release_date,genre_id,duration,director,summary) VALUES (?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS)){
		    	statement.setString(1, film.getTitle());
		    	statement.setDate(2, Date.valueOf(film.getReleaseDate()));
		    	statement.setInt(3, film.getGenre().getId());
		    	statement.setInt(4, film.getDuration());
		    	statement.setString(5, film.getDirector());
		    	statement.setString(6, film.getSummary());
		    	statement.executeUpdate();
		    	//get the id generated during the insertion and add it to the film
		    	try (ResultSet resultSet = statement.getGeneratedKeys()){
					if(resultSet.next() && resultSet != null){
						film.setId(resultSet.getInt(1));
					}
		    	}
		    }
		} 
		catch (SQLException e) {
		    e.printStackTrace();
		}
		return film;
	}
}
