package fr.isen.java2.db.daos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.isen.java2.db.entities.Film;
import fr.isen.java2.db.entities.Genre;

public class FilmDaoTestCase {
	
	private FilmDao filmDao = new FilmDao();
	
	@Before
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS film (\r\n"
				+ "  idfilm INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM film");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first film')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second film')");
		stmt.executeUpdate("INSERT INTO film(idfilm,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third film')");
		stmt.close();
		connection.close();
	}
	
	 @Test
	 public void shouldListFilms() {
		// WHEN
		List<Film> films = filmDao.listFilms();
		// THEN
		assertThat(films).hasSize(3);
		assertThat(films).extracting("id", "title", "releaseDate")
						 .containsOnly(tuple(1, "Title 1", LocalDate.parse("2015-11-26")),tuple(2, "My Title 2", LocalDate.parse("2015-11-14")), tuple(3, "Third title", LocalDate.parse("2015-12-12")));
	 }
	
	 @Test
	 public void shouldListFilmsByGenre() {
		 // WHEN
		 List<Film> films = filmDao.listFilmsByGenre("Comedy");
		 // THEN
		 assertThat(films).hasSize(2);
		 assertThat(films).extracting("id", "title", "releaseDate")
		 				  .containsOnly(tuple(2, "My Title 2", LocalDate.parse("2015-11-14")), tuple(3, "Third title", LocalDate.parse("2015-12-12")));
		 assertThat(films.get(0).getGenre().getId()).isEqualTo(2);
		 assertThat(films.get(1).getGenre().getId()).isEqualTo(2);
	 }
	
	 @Test
	 public void shouldAddFilm() throws Exception {
		 // GIVEN
		 LocalDate localDate = LocalDate.now();
		 Genre genre = new Genre(1,"Drama");
		 Film film = new Film(-1, "Fourth title", localDate, genre, 196, "director 4", "summary of the fourth film");
		 // WHEN
		 film = filmDao.addFilm(film);
		 // THEN
		 Connection connection = DataSourceFactory.getDataSource().getConnection();
		 Statement statement = connection.createStatement();
		 ResultSet resultSet = statement.executeQuery("SELECT * FROM film WHERE title='Fourth title'");
		 assertThat(resultSet.next()).isTrue();
		 assertThat(resultSet.getInt("idfilm")).isEqualTo(film.getId());
		 assertThat(resultSet.getString("title")).isEqualTo("Fourth title");
		 assertThat(resultSet.getDate("release_date").toLocalDate()).isEqualTo(localDate);
		 assertThat(resultSet.next()).isFalse();
		 resultSet.close();
		 statement.close();
		 connection.close();
	 }
}
