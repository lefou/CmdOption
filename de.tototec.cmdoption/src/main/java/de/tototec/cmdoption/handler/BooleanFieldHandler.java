package de.tototec.cmdoption.handler;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import de.tototec.cmdoption.handler.CanHandle.Issue;

/**
 * Apply an one-arg option to a {@link Boolean} (or <code>boolean</code>) field.
 * Evaluated the argument to <code>true</code> if it is "true", "on" or "1".
 *
 * @deprecated {@link BooleanHandler} does the same and also supports methods.
 *             Deprecated since 0.3.0.
 */
@Deprecated
public class BooleanFieldHandler implements CmdOptionHandler {

	private final String[] trueWords;
	private final String[] falseWords;
	private final boolean caseSensitive;

	public BooleanFieldHandler() {
		this(new String[] { "on", "true", "1" }, new String[] { "off", "false", "0" }, false);
	}

	/**
	 * If the list of falseWords is empty or <code>null</code>, any words not in
	 * trueWords is considered as false.
	 *
	 * @param trueWords
	 * @param falseWords
	 * @param caseSensitive
	 */
	public BooleanFieldHandler(final String[] trueWords, final String[] falseWords, final boolean caseSensitive) {
		this.trueWords = trueWords;
		this.falseWords = falseWords;
		this.caseSensitive = caseSensitive;
	}

	public CanHandle canHandle(final AccessibleObject element, final int argCount) {
		CanHandle canHandle = new CanHandle();
		if (argCount != 1) {
			canHandle = canHandle.addIssue(Issue.WRONG_ARG_COUNT);
		}
		if (element instanceof Field) {
			final Field field = (Field) element;
			if (!(field.getType().equals(Boolean.class) || field.getType().equals(boolean.class))) {
				canHandle = canHandle.addIssue(Issue.UNSUPPORTED_TYPE);
			}
			if(Modifier.isFinal(field.getModifiers())) {
				canHandle = canHandle.addIssue(Issue.UNSUPPORTED_FINAL_FIELD);
			}
		} else {
			canHandle = canHandle.addIssue(Issue.NOT_A_FIELD);
		}
		return canHandle;
	}

	public void applyParams(final Object config, final AccessibleObject element, final String[] args,
			final String optionName) throws CmdOptionHandlerException {

		String arg = args[0];
		if (!caseSensitive) {
			arg = arg.toLowerCase();
		}

		Boolean decission = null;

		for (final String word : trueWords) {
			if (arg.equals(caseSensitive ? word : word.toLowerCase())) {
				decission = true;
				break;
			}
		}

		if (decission == null) {
			if (falseWords == null || falseWords.length == 0) {
				decission = false;
			} else {
				for (final String word : falseWords) {
					if (arg.equals(caseSensitive ? word : word.toLowerCase())) {
						decission = false;
						break;
					}
				}
			}
		}

		if (decission == null) {
			throw new CmdOptionHandlerException("Could not parse argument '" + args[0] + "' as boolean parameter.");
		}

		try {
			final Field field = (Field) element;
			field.set(config, decission);
		} catch (final Exception e) {
			throw new CmdOptionHandlerException("Could not apply argument '" + args[0] + "'.", e);
		}

	}
}
