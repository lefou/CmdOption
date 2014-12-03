package de.tototec.cmdoption.handler;

import java.util.EnumSet;

public class CanHandle {

	public enum Issue {
		NOT_A_FIELD,
		NOT_A_METHOD,
		UNSUPPORTED_TYPE,
		WRONG_ARG_COUNT,
		UNSUPPORTED_FINAL_FIELD,
		WRONG_METHOD_PARAMETER_COUNT;
	}

	private final EnumSet<Issue> issues;

	public CanHandle(final EnumSet<Issue> issues) {
		this.issues = issues;
	}

	public CanHandle() {
		this(EnumSet.noneOf(Issue.class));
	}

	public CanHandle addIssue(final Issue issue) {
		final EnumSet<Issue> issues = EnumSet.noneOf(Issue.class);
		issues.addAll(this.issues);
		issues.add(issue);
		return new CanHandle(issues);
	}

	public boolean canHandle() {
		return issues.isEmpty();
	}

	public EnumSet<Issue> getIssues() {
		return issues;
	}

}
