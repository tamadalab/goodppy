package org.goodppy;

/**
 * メインクラス
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String repositoryUrl = "https://github.com/tamadalab/MarryLab.git";
		String localPath = "./repositories/MarryLab";
		RepositoryController repositoryController = new RepositoryController(
				repositoryUrl, localPath);
		repositoryController.GitClone();
		BuildChecker buildChecker = new BuildChecker();
		buildChecker.BuildCheck(localPath);

		return;
	}
}
