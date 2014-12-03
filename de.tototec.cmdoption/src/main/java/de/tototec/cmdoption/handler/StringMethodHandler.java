package de.tototec.cmdoption.handler;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Arrays;

import de.tototec.cmdoption.handler.CanHandle.Issue;

/**
 * Apply an n-arg option to an (setter) method with n parameters of type
 * {@link String}.
 *
 */
public class StringMethodHandler implements CmdOptionHandler {

	public CanHandle canHandle(final AccessibleObject element, final int argCount) {
		CanHandle canHandle = new CanHandle();
		if (element instanceof Method) {
			final Method method = (Method) element;
			if (method.getParameterTypes().length == argCount) {
				boolean areStrings = true;
				for (final Class<?> p : method.getParameterTypes()) {
					areStrings &= String.class.isAssignableFrom(p);
				}
				if (!areStrings) {
					canHandle = canHandle.addIssue(Issue.UNSUPPORTED_TYPE);
				}
			} else {
				canHandle = canHandle.addIssue(Issue.WRONG_ARG_COUNT)
						.addIssue(Issue.WRONG_METHOD_PARAMETER_COUNT);
			}
		} else {
			canHandle = canHandle.addIssue(Issue.NOT_A_METHOD);

		}
		return canHandle;
	}

	public void applyParams(final Object config, final AccessibleObject element, final String[] args,
			final String optionName) throws CmdOptionHandlerException {
		try {
			final Method method = (Method) element;
			method.invoke(config, (Object[]) args);
		} catch (final Exception e) {
			throw new CmdOptionHandlerException("Could not apply parameters: " + Arrays.toString(args) + " to method "
					+ element, e);
		}
	}
}
