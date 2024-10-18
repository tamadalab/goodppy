package org.goodppy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 最終コミット時間と現在時刻の差を計算するクラス
 */
public class CommitTimeDifference {
	private RepositoryController repositoryController;

	/**
	 * コンストラクタ
	 */
	public CommitTimeDifference(String repositoryUrl) {
		this.repositoryController = new RepositoryController(repositoryUrl);
		return;
	}

	/**
	 * 最終コミット時間と現在時刻の差の単位を秒[s]で返す
	 * 
	 * @return 最終コミット時間と現在時刻の差[s]
	 */
	public Long secondTimeDifference() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime lastCommitDateTime = getLastDateTime();
		Long secondsTimeDifference = calculateTimeDifference(currentDateTime, lastCommitDateTime);

		return secondsTimeDifference;
	}

	/**
	 * 最終コミット時間と現在時刻の差を計算する
	 * 
	 * @param currentDateTime    現在時刻
	 * @param lastCommitDateTime 最終コミット日時
	 * @return 最終コミット時間と現在時刻の差[s]
	 */
	public Long calculateTimeDifference(LocalDateTime currentDateTime, LocalDateTime lastCommitDateTime) {
		Duration duration = Duration.between(lastCommitDateTime, currentDateTime);

		return duration.getSeconds();
	}

	/**
	 * 最終コミット時間を取得する
	 * 
	 * @return 最終コミット時間
	 */
	public LocalDateTime getLastDateTime() {
		try {
			Process process = new ProcessBuilder("git", "log", "-1", "--pretty=format:%cd", "--date=iso")
					.directory(new File("./repositories/"+this.repositoryController.ownerAndRepositoryName()))
					.start();
			String commitDateString;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				commitDateString = reader.readLine().trim();
			}
			Integer exitCode = process.waitFor();
			if (exitCode != 0) {
				System.out.println("Failed get last commit date.");
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss xxxx");

			return LocalDateTime.parse(commitDateString, formatter);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();

			return null;
		}
	}
}
