package de.tototec.cmdoption.handler;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.tototec.cmdoption.I18n;
import de.tototec.cmdoption.handler.CanHandle.Issue;

/**
 * Apply an one-arg option to a {@link Integer} (or <code>int</code>) field or
 * method.
 *
 * @since 0.3.1
 */
public class IntegerHandler implements CmdOptionHandler {

	public IntegerHandler() {}

	public CanHandle canHandle(final AccessibleObject element, final int argCount) {
		CanHandle canHandle = new CanHandle();
		if (argCount != 1) {
			canHandle = canHandle.addIssue(Issue.WRONG_ARG_COUNT);
		}
		if (element instanceof Field) {
			final Field field = (Field) element;
			if (!(field.getType().equals(Integer.class) || field.getType().equals(int.class))) {
				canHandle = canHandle.addIssue(Issue.UNSUPPORTED_TYPE);
			}
			if (Modifier.isFinal(field.getModifiers())) {
				canHandle = canHandle.addIssue(Issue.UNSUPPORTED_FINAL_FIELD);
			}
		} else if (element instanceof Method) {
			final Method method = (Method) element;
			if (method.getParameterTypes().length == 1) {
				final Class<?> type = method.getParameterTypes()[0];
				if (!(int.class.equals(type) || Integer.class.equals(type))) {
					canHandle = canHandle.addIssue(Issue.UNSUPPORTED_TYPE);
				}
			} else {
				canHandle = canHandle.addIssue(Issue.WRONG_METHOD_PARAMETER_COUNT);
			}
		}
		return canHandle;
	}

	public void applyParams(final Object config, final AccessibleObject element, final String[] args,
			final String optionName) throws CmdOptionHandlerException {

		int parsedValue = 0;

		try {
			final String arg = args[0];
			parsedValue = Integer.parseInt(arg);
		} catch (final NumberFormatException e) {
			throw new CmdOptionHandlerException(e, I18n.marktr("Could not read integer value \"{0}\"."), args[0]);
		}

		try {
			if (element instanceof Field) {
				final Field field = (Field) element;
				field.set(config, parsedValue);
			} else {
				final Method method = (Method) element;
				method.invoke(config, parsedValue);
			}
		} catch (final Exception e) {
			throw new CmdOptionHandlerException(e, I18n.marktr("Could not apply argument \"{0}\"."), args[0]);
		}

	}
}
