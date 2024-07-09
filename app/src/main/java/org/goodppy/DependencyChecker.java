package org.goodppy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * ライブラリの依存関係をチェックするクラス
 */
public class DependencyChecker {

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

	public DependencyChecker(String repositoryUrl) {
		RepositoryController repositoryController = new RepositoryController(repositoryUrl);
		this.logFileDirectory = "./logs/dependency/"
				+ repositoryController.getOwner()
				+ "/"
				+ repositoryController.getRepositoryName()
				+ "/";
		this.repositoryName = repositoryController.getRepositoryName();
		this.repositoryUrl = repositoryUrl;

		return;
	}

	/**
	 * 依存関係に脆弱性がないかをチェックする
	 */
	public void dependencyCheck(Path localPath) {
		try {
			System.out.println("Start check dependencies...");
			ProcessBuilder builder = new ProcessBuilder();
			// builder.command("./gradlew", "dependencyCheckAnalyze"); // 依存関係を調べる
			builder.command("./dependency-check.sh", "--project", getRepositoryName(), "--scan", localPath.toString()); // 依存関係を調べる
			// builder.directory(localPath.toFile());
			builder.directory(new File("./"));
			createFile();
			builder.redirectErrorStream(true);
			builder.redirectOutput(generateLogfilePath().toFile());
			Process process = builder.start();
			long start = System.nanoTime();
			Integer exitCode = process.waitFor();
			if (exitCode != 0) {
				System.out.println(" Faild");
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
	 * ログファイルのパスを生成する
	 * 
	 * @return ログファイルのパス
	 */
	public Path generateLogfilePath() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		// RepositoryController repositoryController = new
		// RepositoryController(getRepositoryUrl());
		String logFile = getLogfileDirectory()
				// + repositoryController.getOwner()
				// + "_"
				// + repositoryController.getRepositoryName()
				// + "_"
				+ sdf.format(calendar.getTime())
				+ ".log";
		Path logFilePath = Paths.get(logFile);
		return logFilePath;
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
