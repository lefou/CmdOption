package de.tototec.cmdoption.handler;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import de.tototec.cmdoption.handler.CanHandle.Issue;

/**
 * Apply an one-arg option to a field of type {@link String}.
 *
 */
public class StringFieldHandler implements CmdOptionHandler {

	public CanHandle canHandle(final AccessibleObject element, final int argCount) {
		CanHandle canHandle = new CanHandle();
		if (argCount != 1) {
			canHandle = canHandle.addIssue(Issue.WRONG_ARG_COUNT);
		}
		if (element instanceof Field) {
			final Field field = (Field) element;
			if (!(field.getType().equals(String.class))) {
				canHandle = canHandle.addIssue(Issue.UNSUPPORTED_TYPE);
			}
			if (Modifier.isFinal(field.getModifiers())) {
				canHandle = canHandle.addIssue(Issue.UNSUPPORTED_FINAL_FIELD);
			}
		} else {
			canHandle = canHandle.addIssue(Issue.NOT_A_FIELD);
		}
		return canHandle;
	}

	public void applyParams(final Object config, final AccessibleObject element, final String[] args,
			final String optionName) throws CmdOptionHandlerException {

		try {
			final Field field = (Field) element;
			field.set(config, args[0]);
		} catch (final Exception e) {
			// TODO better message
			throw new CmdOptionHandlerException("Could not apply parameters: " + Arrays.toString(args) + " to field "
					+ element, e);
		}

	}

}
