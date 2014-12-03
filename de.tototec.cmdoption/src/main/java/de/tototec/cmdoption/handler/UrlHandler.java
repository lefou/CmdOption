package de.tototec.cmdoption.handler;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import de.tototec.cmdoption.handler.CanHandle.Issue;

public class UrlHandler implements CmdOptionHandler {

	public CanHandle canHandle(final AccessibleObject element, final int argCount) {
		CanHandle canHandle = new CanHandle();
		if (argCount != 1) {
			canHandle = canHandle.addIssue(Issue.WRONG_ARG_COUNT);
		}
		if (element instanceof Field) {
			final Field field = (Field) element;
			if (!(field.getType().equals(URL.class))) {
				canHandle = canHandle.addIssue(Issue.UNSUPPORTED_TYPE);
			}
			if (Modifier.isFinal(field.getModifiers())) {
				canHandle = canHandle.addIssue(Issue.UNSUPPORTED_FINAL_FIELD);
			}
		} else if (element instanceof Method) {
			final Class<?>[] params = ((Method) element).getParameterTypes();
			if (params.length == 1) {
				if (!params[0].equals(URL.class)) {
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
		try {
			final URL url = new URL(args[0]);
			if (element instanceof Field) {
				((Field) element).set(config, url);
			} else if (element instanceof Method) {
				((Method) element).invoke(config, url);
			}
		} catch (final MalformedURLException e) {
			throw new CmdOptionHandlerException("Invalid url: \"" + args[0] + "\"", e);
		} catch (final Exception e) {
			// TODO better message
			throw new CmdOptionHandlerException("Could not apply parameters: " + Arrays.toString(args)
					+ " to field/method " + element, e);
		}

	}
}
