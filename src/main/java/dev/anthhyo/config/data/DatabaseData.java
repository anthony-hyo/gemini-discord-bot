package dev.anthhyo.config.data;

public record DatabaseData(String host, int port, String user, String password, String database) {

	public String JDBC_URL() {
		return "jdbc:mariadb://%s:%s/%s".formatted(host(), port(), database());
	}

}
