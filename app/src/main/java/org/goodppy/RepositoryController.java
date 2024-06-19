package org.goodppy;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * 評価するリポジトリに関する操作を行うクラス
 */
public class RepositoryController {
	private String repositoryUrl;

	private String localPath;

	/**
	 * @param repositoryUrl
	 * @param localPath
	 */
	public RepositoryController(String repositoryUrl, String localPath) {
		this.repositoryUrl = repositoryUrl;
		this.localPath = localPath;
	}

	public void GitClone() {
		try {
			System.out.println("Repository is being cloned ...");
			Git.cloneRepository()
					.setURI(repositoryUrl)
					.setDirectory(new File(localPath))
					.call();
			System.out.println("Repository has been cloned.");
		} catch (GitAPIException e) {
			e.printStackTrace();
			System.out.println("Repository has been failed to clone.");
		}

		return;
	}

}
