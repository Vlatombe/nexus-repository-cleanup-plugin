package com.jostens.nexus.cleanup

import java.util.ArrayList;
import java.util.Collection;
import org.sonatype.nexus.proxy.ItemNotFoundException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.repository.Repository;

class RepositoryCleanup {

	/**
	 * Search a repository finding all artifacts that match the given regular expression, and are older than the given age.
	 * @param repository Repository to search.
	 * @param pattern REGEX pattern to match artifact name against.
	 * @param age Artifact age in days.
	 * @param path Path to search.
	 * @return Collection of all items in repository that match criteria.
	 * @throws Exception
	 */
	public static Collection<StorageItem> findArtifacts(Repository repository, String pattern, int age, String path) throws Exception {
		def storageItems = new ArrayList();
		def items = null;
		def currentTime = new Date() - age;
		def request = new ResourceStoreRequest(path);

		// Get Items at this path.
		try {
			items = repository.list(request);
		} catch (ItemNotFoundException e) {
			items = null;
		}

		// For each item found, check if it matches criteria, and recursively check it's children.
		items.each { item->
			if (item.getName() ==~ pattern) {
				if (currentTime > (new Date(item.getModified())) ) {
					storageItems << item;
				}
			} else {
				storageItems += RepositoryCleanup.findArtifacts(repository, pattern, age, item.getPath());
			}
		}


		return storageItems;
	}

}