package org.goodppy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * メインクラス
 */
public class Main {
	/**
	 * メインメソッド
	 * 
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		String repositoriesList = "./src/main/resource/repositories.txt";
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(repositoriesList))) {
			String repositoryUrl;
			while ((repositoryUrl = bufferedReader.readLine()) != null) {
				SurvivalScoreCalculator survivalScoreCalculator = new SurvivalScoreCalculator(repositoryUrl);
				if (survivalScoreCalculator.checkWeight() != 0) {
					return;
				}
				System.out.println("----------------------------------------");
				System.out.printf("Start the evaluation of %s\n", repositoryUrl);
				RepositoryController repositoryController = new RepositoryController(repositoryUrl);
				repositoryController.gitClone();
				BuildChecker buildChecker = new BuildChecker(repositoryUrl);
				List<String> buildResult = buildChecker.buildCheck(repositoryController.getLocalPath());
				// if (buildResult.stream().allMatch(result -> result.equals("failed"))) {
				// 	System.out.println("Build failed for all Java LTS versions.");
				// 	continue;
				// }
				GitHubDataController gitHubDataController = new GitHubDataController(repositoryUrl);
				String[] gitHubDataList = gitHubDataController.collectData();
				Scoring scoring = new Scoring(repositoryUrl);
				Double dependencyScore = scoring.evaluateDependency();
				scoring.writeCsv(buildResult);
				survivalScoreCalculator.calculateSurvivalScore(gitHubDataList, dependencyScore);
				System.out.printf("End the evaluation of %s\n", repositoryUrl);
			}
			System.out.println("----------------------------------------");
			System.out.println("End the evaluation of all repositories");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}
}
