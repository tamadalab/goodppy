package org.goodppy;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * 評価するリポジトリについて操作を行うクラス
 */
public class RepositoryController {

	/**
	 * クローン先のディレクトリのパス
	 */
	private String localPath;

	/**
	 * リポジトリのオーナー名
	 */
	private String owner;

	/**
	 * リポジトリ名
	 */
	private String repositoryName;

	/**
	 * リポジトリのURL
	 */
	private String repositoryUrl;

	/**
	 * コンストラクタ
	 */
	public RepositoryController(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
		this.owner = makeUrlParts()[makeUrlParts().length - 2];
		this.repositoryName = makeUrlParts()[makeUrlParts().length - 1].replace(".git", "");
		this.localPath = "./repositories/" + getOwner() + "/" + getRepositoryName(); // ./repositories/owner/repositoryName

		return;
	}

	/**
	 * クローン先のディレクトリのパスを取得する
	 * 
	 * @return クローン先のディレクトリのパス
	 */
	public String getLocalPath() {
		return this.localPath;
	}

	/**
	 * リポジトリのオーナー名を取得する
	 * 
	 * @return リポジトリのオーナー名
	 */
	public String getOwner() {
		return this.owner;
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

	/**
	 * リポジトリをクローンする
	 */
	public void gitClone() {
		try {
			System.out.println("Repository is being cloned...");
			Git.cloneRepository()
					.setURI(getRepositoryUrl())
					.setDirectory(new File(getLocalPath()))
					.call();
			System.out.println("Repository has been cloned.");
		} catch (GitAPIException e) {
			e.printStackTrace();
			System.out.println("Repository has been failed to clone.");
		}

		return;
	}

	/**
	 * リポジトリのURLを「/」で区切る
	 * 
	 * @return リポジトリのURLのパース結果
	 */
	public String[] makeUrlParts() {
		String[] urlParts = getRepositoryUrl().split("/");

		return urlParts;
	}

	// public void setLocalPath(String localPath) {
	// this.localPath = localPath;

	// return;
	// }

	// public void setOwner(String owner) {
	// this.owner = owner;

	// return;
	// }

	// public void setRepositoryName(String repositoryName) {
	// this.repositoryName = repositoryName;

	// return;
	// }

	// public void setRepositoryUrl(String repositoryUrl) {
	// this.repositoryUrl = repositoryUrl;

	// return;
	// }
}
