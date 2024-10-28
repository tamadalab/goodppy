package org.goodppy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Multimap;

/**
 * ライブラリの依存関係をチェックするクラス
 */
public class DependencyChecker {

	/**
	 * NVD APIキー
	 */
	private String apiKey;

	/**
	 * ログファイルを保存するディレクトリのパス
	 */
	private String logFileDirectory;

	/**
	 * リポジトリ名
	 */
	private String repositoryName;

	/**
	 * リポジトリのURL
	 */
	private String repositoryUrl;

	/**
	 * 評価するリポジトリについて操作を行う
	 */
	private RepositoryController repositoryController;

	/**
	 * コンストラクタ
	 * 
	 * @param repositoryUrl リポジトリのURL
	 */
	public DependencyChecker(String repositoryUrl) {
		this.apiKey = getApiKey();
		this.repositoryController = new RepositoryController(repositoryUrl);
		// ./logs/dependency/owner/repositoryName/
		this.logFileDirectory = "./logs/dependency/" + this.repositoryController.ownerAndRepositoryName();
		this.repositoryName = this.repositoryController.getRepositoryName();
		this.repositoryUrl = repositoryUrl;

		return;
	}

	/**
	 * ログファイルを生成する
	 */
	public void createFile() {
		File logDirectory = new File(getLogfileDirectory());
		File logFile = new File(generateLogfilePath().toString());
		try {
			if (logDirectory.exists() == false) {
				logDirectory.mkdirs();
			}
			if (logFile.exists() == false)
				logFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	/**
	 * レポートから得たデータを処理する
	 */
	public Integer[] csvDataControl() {
		CsvController csvController = new CsvController(getRepositoryUrl());
		Multimap<String, List<String>> dependencies = csvController.readCsv();
		Integer[] scores = { 0, 0, 0, 0, 0 };
		for (Map.Entry<String, List<String>> entry : dependencies.entries()) {
			// String key = entry.getKey();
			// String dependencyName = key;
			// String vulnerability = entry.getValue().get(0);
			String baseSeverity = entry.getValue().get(1);
			// String baseScore = entry.getValue().get(2);
			scores = markDependency(baseSeverity, scores);
		}

		return scores;
	}

	/**
	 * 依存関係に脆弱性がないかをチェックする
	 */
	public void dependencyCheck(Path localPath) {
		try {
			System.out.println("Start check dependencies...");
			ProcessBuilder builder = new ProcessBuilder();
			builder.command("./src/main/resource/dependency-check/bin/dependency-check.sh",
					"--nvdApiKey", this.apiKey,
					"--project", getRepositoryName(),
					"--scan", localPath.toString(),
					"--out", "./dependency_reports/" + this.repositoryController.ownerAndRepositoryName(),
					"--format", "CSV"); // 依存関係を調べる
			builder.directory(new File("./"));
			createFile();
			builder.redirectErrorStream(true);
			builder.redirectOutput(generateLogfilePath().toFile());
			Process process = builder.start();
			long start = System.nanoTime();
			Integer exitCode = process.waitFor();
			if (exitCode != 0) {
				System.out.println("Faild");
				long end = System.nanoTime();
				System.out.println("Time taked to check dependencies : " + (end - start) + "ns");

				return;
			}
			System.out.println("Success");
			long end = System.nanoTime();
			System.out.println("Time taked to check dependencies : " + (end - start) + "ns");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return;
	}

	/**
	 * 依存関係について評価する
	 * 
	 * @param baseSeverity 脆弱性の深刻度
	 * @param scores       スコア
	 * @return スコア
	 */
	public Integer[] markDependency(String baseSeverity, Integer[] scores) {
		switch (baseSeverity) {
			case "CRITICAL" -> {
				scores[0] += 4;
			}
			case "HIGH" -> {
				scores[1] += 3;
			}
			case "MEDIUM" -> {
				scores[2] += 2;
			}
			case "LOW" -> {
				scores[3] += 1;
			}
			default -> {

			}
		}
		scores[4]++;

		return scores;
	}

	/**
	 * ログファイルのパスを生成する
	 * 
	 * @return ログファイルのパス
	 */
	public Path generateLogfilePath() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String logFile = getLogfileDirectory()
				// + this.repositoryController.getOwner()
				// + "_"
				// + this.repositoryController.getRepositoryName()
				// + "_"
				+ sdf.format(calendar.getTime())
				+ ".log";
		Path logFilePath = Paths.get(logFile);

		return logFilePath;
	}

	/**
	 * APIキーを取得する
	 * 
	 * @return APIキー
	 */
	public String getApiKey() {
		try {
			String apiKey;
			File file = new File("./src/main/resource/dependency-check_APIkey");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			apiKey = reader.readLine();
			reader.close();
			return apiKey;
		} catch (IOException e) {
			e.printStackTrace();

			return new String();
		}
	}

	/**
	 * ログファイルを保存するディレクトリのパスを取得する
	 * 
	 * @return ログファイルを保存するディレクトリのパス
	 */
	public String getLogfileDirectory() {
		return this.logFileDirectory;
	}

	/**
	 * リポジトリ名を取得する
	 * 
	 * @return リポジトリ名
	 */
	public String getRepositoryName() {
		return this.repositoryName;
	}

	/**
	 * リポジトリのURLを取得する
	 * 
	 * @return リポジトリのURL
	 */
	public String getRepositoryUrl() {
		return this.repositoryUrl;
	}
}
