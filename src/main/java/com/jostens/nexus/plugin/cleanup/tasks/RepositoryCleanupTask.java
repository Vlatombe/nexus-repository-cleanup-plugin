package com.jostens.nexus.plugin.cleanup.tasks;

import java.util.Collection;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.item.RepositoryItemUid;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.maven.MavenRepository;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.scheduling.AbstractNexusRepositoriesTask;
import org.sonatype.nexus.scheduling.NexusScheduler;
import org.sonatype.scheduling.SchedulerTask;

import com.google.inject.Inject;
import com.jostens.nexus.cleanup.RepositoryCleanup;
import com.jostens.nexus.plugin.cleanup.tasks.descriptors.RepositoryCleanupTaskDescriptor;

@Component(role = SchedulerTask.class,hint = RepositoryCleanupTaskDescriptor.ID,instantiationStrategy = "per-lookup")
public class RepositoryCleanupTask extends AbstractNexusRepositoriesTask<Object> {

	@Inject
	private RepositoryRegistry repositoryRegistry;

	@Requirement
    private NexusScheduler nexusScheduler;

	@Override
	protected String getAction() {
		return "Repository Cleanup Task";
	}

	@Override
	protected String getMessage() {
		return "Running Repository Cleanup Task";
	}

	@Override
	protected Object doRun() throws Exception {

		getLogger().info("Beginning repository cleanup...");
		String repo = this.getRepoId().replaceAll("repo_", "");
		String pattern = this.getPattern();
		int age = Integer.parseInt(this.getAge());
		boolean preview = this.getPreview();

		getLogger().info("Repository ID: " + repo);
		getLogger().info("Pattern: " + pattern);
		getLogger().info("Age: " + age);
		getLogger().info("Preview: " + preview);

		// Get a Repository Object.
		Repository repository = repositoryRegistry.getRepository(repo);

		Collection<StorageItem> storageItems = RepositoryCleanup.findArtifacts(repository, pattern, age, RepositoryItemUid.PATH_ROOT);
		if ((storageItems != null) && (storageItems.size() > 0)) {
			for (StorageItem storageItem : storageItems) {

				getLogger().info("Deleting Item: " + storageItem.getName() + " @ " + storageItem.getParentPath());
				if (!preview) {
					repository.deleteItem(new ResourceStoreRequest(storageItem.getPath()));
				}
			}
			getLogger().info("Rebuilding Repository Metadata: " + repository.getId());
			if (!preview) {
				MavenRepository mavenRepository = repository.adaptToFacet( MavenRepository.class );
				mavenRepository.recreateMavenMetadata(new ResourceStoreRequest(RepositoryItemUid.PATH_ROOT));
			}

		}

		getLogger().info("Finished repository cleanup...");

		return "success";
	}

	public String getRepoId() {
		return getParameters().get(RepositoryCleanupTaskDescriptor.REPO_OR_GROUP_FIELD_ID);
	}

	public void setRepoId(String repoId) {
		getParameters().put(RepositoryCleanupTaskDescriptor.REPO_OR_GROUP_FIELD_ID, repoId);
	}

	public String getPattern() {
		return getParameters().get(RepositoryCleanupTaskDescriptor.PATTERN_URL_FIELD_ID);
	}

	public void setPattern(String pattern) {
		getParameters().put(RepositoryCleanupTaskDescriptor.PATTERN_URL_FIELD_ID, pattern);
	}

	public String getAge() {
		return getParameters().get(RepositoryCleanupTaskDescriptor.AGE_URL_FIELD_ID);
	}

	public void setAge(String age) {
		getParameters().put(RepositoryCleanupTaskDescriptor.AGE_URL_FIELD_ID, age);
	}

	public boolean getPreview() {
		return Boolean.parseBoolean(getParameter(RepositoryCleanupTaskDescriptor.PREVIEW_FIELD_ID));
	}

	public void setPreview(boolean preview) {
		getParameters().put(RepositoryCleanupTaskDescriptor.PREVIEW_FIELD_ID, Boolean.toString(preview));
	}

	public RepositoryRegistry getRepositoryRegistry() {
		return repositoryRegistry;
	}

	public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
		this.repositoryRegistry = repositoryRegistry;
	}

	public NexusScheduler getNexusScheduler() {
		return nexusScheduler;
	}

	public void setNexusScheduler(NexusScheduler nexusScheduler) {
		this.nexusScheduler = nexusScheduler;
	}

}
