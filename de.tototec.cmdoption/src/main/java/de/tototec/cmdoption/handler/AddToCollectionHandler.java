package de.tototec.cmdoption.handler;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Collection;

import de.tototec.cmdoption.handler.CanHandle.Issue;

/**
 * Add an one-arg option argument to a collection of strings.
 */
public class AddToCollectionHandler implements CmdOptionHandler {

	public void applyParams(final Object config, final AccessibleObject element, final String[] args,
			final String optionName) {
		try {
			final Field field = (Field) element;
			@SuppressWarnings("unchecked")
			final Collection<String> collection = (Collection<String>) field.get(config);
			collection.add(args[0]);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public CanHandle canHandle(final AccessibleObject element, final int argCount) {
		CanHandle canHandle = new CanHandle();
		if (argCount != 1) {
			canHandle = canHandle.addIssue(Issue.WRONG_ARG_COUNT);
		}
		if (!(element instanceof Field && Collection.class.isAssignableFrom(((Field) element).getType()))) {
			canHandle = canHandle.addIssue(Issue.UNSUPPORTED_TYPE);
		}
		return canHandle;
	}
}
