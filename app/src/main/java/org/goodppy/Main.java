package org.goodppy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
				System.out.println("----------------------------------------");
				System.out.printf("Start the evaluation of %s\n", repositoryUrl);
				RepositoryController repositoryController = new RepositoryController(repositoryUrl);
				repositoryController.gitClone();
				BuildChecker buildChecker = new BuildChecker(repositoryUrl);
				buildChecker.buildCheck(repositoryController.getLocalPath());
				DependencyChecker dependencyChecker = new DependencyChecker(repositoryUrl);
				dependencyChecker.dependencyCheck(repositoryController.getLocalPath());
				Evaluate evaluate = new Evaluate(repositoryUrl);
				evaluate.evaluateDependency();
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
