package com.jostens.nexus.plugin.cleanup.tasks.descriptors;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.formfields.CheckboxFormField;
import org.sonatype.nexus.formfields.FormField;
import org.sonatype.nexus.formfields.RepoOrGroupComboFormField;
import org.sonatype.nexus.formfields.StringTextFormField;
import org.sonatype.nexus.tasks.descriptors.AbstractScheduledTaskDescriptor;
import org.sonatype.nexus.tasks.descriptors.ScheduledTaskDescriptor;

@Component(role = ScheduledTaskDescriptor.class,hint = "RepositoryCleanup",description = "Repository Cleanup Task")
public class RepositoryCleanupTaskDescriptor extends AbstractScheduledTaskDescriptor {

	public static final String ID = "RepositoryCleanupTask";

	public static final String REPO_OR_GROUP_FIELD_ID = "repositoryId";
	public static final String PATTERN_URL_FIELD_ID = "pattern";
	public static final String AGE_URL_FIELD_ID = "age";
	public static final String PREVIEW_FIELD_ID = "preview";

	private final RepoOrGroupComboFormField repoField = new RepoOrGroupComboFormField(REPO_OR_GROUP_FIELD_ID, true);
	private final StringTextFormField patternField = new StringTextFormField(PATTERN_URL_FIELD_ID, "Regular Expression", "Regular Expression to find artifacts.", true);
	private final StringTextFormField age = new StringTextFormField(AGE_URL_FIELD_ID, "Artifact retention (days)", "The job will purge all artifacts older then the entered number of days.", true);
	private final CheckboxFormField preview = new CheckboxFormField(PREVIEW_FIELD_ID, "preview", "Tick to preview the result (see logs)", true);

	public String getId() {
		return ID;
	}

	public String getName() {
		return "Repository Cleanup Task";
	}

	public List<FormField> formFields() {
		List<FormField> fields = new ArrayList<FormField>();

		fields.add(repoField);
		fields.add(patternField);
		fields.add(age);
		fields.add(preview);

		return fields;
	}

}
